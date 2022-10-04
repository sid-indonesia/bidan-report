package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListUtil.createContactListRequest;
import static org.sidindonesia.bidanreport.util.CSVUtil.DHO;
import static org.sidindonesia.bidanreport.util.CSVUtil.FULL_NAME;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.config.property.SchedulingProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.web.response.RetrieveContactListResponse;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.sidindonesia.bidanreport.util.CSVUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class IntroMessageService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final QontakProperties qontakProperties;
	private final BroadcastMessageService broadcastMessageService;
	private final ContactListService contactListService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;
	private final SchedulingProperties schedulingProperties;

	@Scheduled(fixedRateString = "${scheduling.intro-message.fixed-rate-in-ms}", initialDelayString = "${scheduling.intro-message.initial-delay-in-ms}")
	public void sendIntroMessageToNewMothersViaWhatsApp() throws IOException, InterruptedException {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\"...");

		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() throws IOException, InterruptedException {
		List<MotherIdentityWhatsAppProjection> newPregnantWomenIdentities = motherIdentityRepository
			.findAllPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherIdentity().getPregnantMotherLastId());

		if (!newPregnantWomenIdentities.isEmpty()) {
			broadcastPregnantWomenIntroMessageTo(newPregnantWomenIdentities, "mother_identity");

			lastIdProperties.getMotherIdentity().setPregnantMotherLastId(
				newPregnantWomenIdentities.get(newPregnantWomenIdentities.size() - 1).getEventId());
			log.info("Scheduled \"Send Join Notification via WhatsApp\" for new enrolled pregnant women completed.");
		}
	}

	private void processRowsFromMotherEdit() throws IOException, InterruptedException {
		List<MotherIdentityWhatsAppProjection> editedPregnantWomenIds = motherEditRepository
			.findAllPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherEdit().getPregnantMotherLastId());

		if (!editedPregnantWomenIds.isEmpty()) {
			broadcastPregnantWomenIntroMessageTo(editedPregnantWomenIds, "mother_edit");

			lastIdProperties.getMotherEdit()
				.setPregnantMotherLastId(editedPregnantWomenIds.get(editedPregnantWomenIds.size() - 1).getEventId());
			log.info("Scheduled \"Send Join Notification via WhatsApp\" for edited pregnant women completed.");
		}
	}

	private void broadcastPregnantWomenIntroMessageTo(List<MotherIdentityWhatsAppProjection> pregnantWomenIdentities,
		String fromTable) throws IOException, InterruptedException {

		// Create contact list CSV
		String contactsCsvFileName = qontakProperties.getWhatsApp().getIntroMessageContactListCsvAbsoluteFileName()
			.replace(".csv", "_" + fromTable + ".csv");
		CSVUtil.createContactListCSVFileForIntroMessage(pregnantWomenIdentities, contactsCsvFileName,
			qontakProperties.getWhatsApp().getDistrictHealthOfficeName());

		String campaignName = ZonedDateTime.now() + " " + qontakProperties.getWhatsApp().getDistrictHealthOfficeName()
			+ ", " + fromTable + " (Intro Message)";
		// Hit API post contact list, get contact_list_id
		String contactListId = contactListService
			.sendCreateContactListRequestToQontakAPI(createContactListRequest(campaignName, contactsCsvFileName));

		if (contactListId != null) {
			// Give Qontak some time to process the contact list
			// (because the API is asynchronous and currently
			// there is no "synchronously Create Contact List API")
			Thread.sleep(schedulingProperties.getContactList().getInitialDelayInMs()); // give initial 5 seconds
			for (int i = 0; i < schedulingProperties.getContactList().getMaxNumberOfRetries(); i++) {
				RetrieveContactListResponse response = contactListService
					.retrieveContactListRequestToQontakAPI(contactListId);

				if ("success".equalsIgnoreCase(response.getData().getProgress())) {
					broadcastBulk(fromTable, pregnantWomenIdentities, campaignName, contactListId);
					return;
				}

				Thread.sleep(schedulingProperties.getContactList().getDelayInMs());
			}
			log.error(
				"\"Send Intro Message via WhatsApp\" failed due to Contact List not available after {} retries with interval {}ms",
				schedulingProperties.getContactList().getMaxNumberOfRetries(),
				schedulingProperties.getContactList().getDelayInMs());

		} else {
			log.error(
				"\"Send Intro Message via WhatsApp\" for enrolled pregnant women failed due to error when POST contact list. ("
					+ fromTable + ")");
		}
	}

	private void broadcastBulk(String fromTable, List<MotherIdentityWhatsAppProjection> pregnantWomenIdentities,
		String campaignName, String contactListId) {
		// Broadcast to contact_list
		boolean isSuccess = broadcastBulkMessageViaWhatsApp(
			qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId(), contactListId, campaignName);

		log.info("\"Send Intro Message via WhatsApp\" for enrolled pregnant women completed. (" + fromTable + ")");

		if (isSuccess) {
			log.info(
				"{} enrolled pregnant women have been given intro message via WhatsApp successfully as bulk broadcast request.",
				pregnantWomenIdentities.size());
			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId(),
				"intro_pregnant_woman", pregnantWomenIdentities.size(), 0);
		} else {
			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId(),
				"intro_pregnant_woman", 0, pregnantWomenIdentities.size());
		}
	}

	private boolean broadcastBulkMessageViaWhatsApp(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = createIntroMessageRequestBody(messageTemplateId, contactListId, campaignName);
		return broadcastMessageService.sendBroadcastRequestToQontakAPI(requestBody);
	}

	private BroadcastRequest createIntroMessageRequestBody(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(campaignName,
			messageTemplateId, contactListId);

		setParametersForIntroMessage(requestBody);
		return requestBody;
	}

	private void setParametersForIntroMessage(BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", FULL_NAME);
		parameters.addBodyWithValues("2", DHO);
		requestBody.setParameters(parameters);
	}
}