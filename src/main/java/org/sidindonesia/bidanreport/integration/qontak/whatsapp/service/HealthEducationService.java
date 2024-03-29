package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;
import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListUtil.createContactListRequest;
import static org.sidindonesia.bidanreport.util.CSVUtil.CALC_GESTATIONAL;
import static org.sidindonesia.bidanreport.util.CSVUtil.FULL_NAME;
import static org.sidindonesia.bidanreport.util.CSVUtil.PREGNA_TRIMESTER;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.HealthEducationProjection;
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
public class HealthEducationService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final ContactListService contactListService;

	@Scheduled(cron = "${scheduling.health-education.cron}", zone = "${scheduling.health-education.zone}")
	public void sendHealthEducationsToEnrolledMothers() throws IOException, InterruptedException {
		log.debug("Executing scheduled \"Send Health Education via WhatsApp\"...");
		log.debug("Send scheduled health education messages to all pregnant mothers "
			+ "with current_date between last_menstrual_period_date and expected_delivery_date "
			+ "and not recorded in `anc_close` within that period.");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() throws IOException, InterruptedException {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherIdentityRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage, "mother_identity");
	}

	private void processRowsFromMotherEdit() throws IOException, InterruptedException {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherEditRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage, "mother_edit");
	}

	private void broadcastHealthEducationMessageTo(
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage, String fromTable)
		throws IOException, InterruptedException {

		List<HealthEducationProjection> filteredPregnantWomen = allPregnantWomenToBeGivenHealthEducationMessage
			.parallelStream()
			.filter(healthEducationProjection -> healthEducationProjection.getCalculatedGestationalAge() != null
				&& healthEducationProjection.getPregnancyTrimester() != null)
			.collect(toList());
		if (!filteredPregnantWomen.isEmpty()) {

			// Create contact list CSV
			String contactsCsvFileName = qontakProperties.getWhatsApp()
				.getHealthEducationContactListCsvAbsoluteFileName().replace(".csv", "_" + fromTable + ".csv");
			CSVUtil.createContactListCSVFileForHealthEducation(filteredPregnantWomen, contactsCsvFileName);

			String campaignName = ZonedDateTime.now() + " "
				+ qontakProperties.getWhatsApp().getDistrictHealthOfficeName() + ", " + fromTable
				+ " (Health Education)";
			// Hit API post contact list, get contact_list_id
			String contactListId = contactListService
				.sendCreateContactListRequestToQontakAPI(createContactListRequest(campaignName, contactsCsvFileName));

			if (contactListId != null) {

				if (contactListService.tryRetrieveContactListByIdMultipleTimes(contactListId)) {
					broadcastBulk(fromTable, filteredPregnantWomen, campaignName, contactListId);
				}

			} else {
				log.error(
					"\"Send Health Education via WhatsApp\" for enrolled pregnant women failed due to error when POST contact list. ("
						+ fromTable + ")");
			}
		}
	}

	private void broadcastBulk(String fromTable, List<HealthEducationProjection> filteredPregnantWomen,
		String campaignName, String contactListId) throws InterruptedException {
		// Broadcast to contact_list
		boolean isSuccess = broadcastBulkMessageViaWhatsApp(
			qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId(), contactListId, campaignName);

		log.info("\"Send Health Education via WhatsApp\" for enrolled pregnant women completed. (" + fromTable + ")");

		if (isSuccess) {
			log.info(
				"{} enrolled pregnant women have been given health education via WhatsApp successfully as bulk broadcast request.",
				filteredPregnantWomen.size());
		}
	}

	private boolean broadcastBulkMessageViaWhatsApp(String messageTemplateId, String contactListId, String campaignName)
		throws InterruptedException {
		BroadcastRequest requestBody = createHealthEducationMessageRequestBody(messageTemplateId, contactListId,
			campaignName);
		return broadcastMessageService.sendBroadcastRequestToQontakAPI(requestBody);
	}

	private BroadcastRequest createHealthEducationMessageRequestBody(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(campaignName,
			messageTemplateId, contactListId);

		setParametersForHealthEducationMessage(requestBody);
		return requestBody;
	}

	private void setParametersForHealthEducationMessage(BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", FULL_NAME);
		requestBody.setParameters(parameters);
	}
}
