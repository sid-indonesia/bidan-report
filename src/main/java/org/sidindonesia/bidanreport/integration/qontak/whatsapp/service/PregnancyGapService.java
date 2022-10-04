package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;
import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListUtil.createContactListRequest;
import static org.sidindonesia.bidanreport.util.CSVUtil.ANC_DATE;
import static org.sidindonesia.bidanreport.util.CSVUtil.DIASTOLIC_BP;
import static org.sidindonesia.bidanreport.util.CSVUtil.FETAL_HEART_RATE;
import static org.sidindonesia.bidanreport.util.CSVUtil.FETAL_PRESENTATI;
import static org.sidindonesia.bidanreport.util.CSVUtil.FULL_NAME;
import static org.sidindonesia.bidanreport.util.CSVUtil.GESTATIONAL_AGE;
import static org.sidindonesia.bidanreport.util.CSVUtil.GIVEN_IFA_TABLET;
import static org.sidindonesia.bidanreport.util.CSVUtil.GIVEN_TT_INJECTI;
import static org.sidindonesia.bidanreport.util.CSVUtil.GLUCOSE_140_MGDL;
import static org.sidindonesia.bidanreport.util.CSVUtil.HAS_HBSAG;
import static org.sidindonesia.bidanreport.util.CSVUtil.HAS_HIV;
import static org.sidindonesia.bidanreport.util.CSVUtil.HAS_PROTEINURIA;
import static org.sidindonesia.bidanreport.util.CSVUtil.HAS_SYPHILIS;
import static org.sidindonesia.bidanreport.util.CSVUtil.HAS_THALASEMIA;
import static org.sidindonesia.bidanreport.util.CSVUtil.HB_LEVEL_RESULT;
import static org.sidindonesia.bidanreport.util.CSVUtil.HEIGHT_IN_CM;
import static org.sidindonesia.bidanreport.util.CSVUtil.MUAC_IN_CM;
import static org.sidindonesia.bidanreport.util.CSVUtil.SYSTOLIC_BP;
import static org.sidindonesia.bidanreport.util.CSVUtil.TETANUS_T_IMM_ST;
import static org.sidindonesia.bidanreport.util.CSVUtil.UTERINE_F_HEIGHT;
import static org.sidindonesia.bidanreport.util.CSVUtil.WEIGHT_IN_KG;

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
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.sidindonesia.bidanreport.service.LastIdService;
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
public class PregnancyGapService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final LastIdProperties lastIdProperties;
	private final LastIdService lastIdService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;
	private final SchedulingProperties schedulingProperties;
	private final ContactListService contactListService;

	@Scheduled(fixedRateString = "${scheduling.pregnancy-gap.fixed-rate-in-ms}", initialDelayString = "${scheduling.pregnancy-gap.initial-delay-in-ms}")
	public void sendPregnancyGapMessageToEnrolledMothers() throws IOException, InterruptedException {
		log.debug("Executing scheduled \"Inform Pregnancy Gap via WhatsApp\"...");
		log.debug("Send pregnancy gap message to all mothers according to their latest ANC visit");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();

		lastIdService.syncANCVisitPregnancyGapLastId();
	}

	private void processRowsFromMotherIdentity() throws IOException, InterruptedException {
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherIdentityRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(lastIdProperties.getAncVisitPregnancyGapLastId())
			.parallelStream().filter(motherIdentity -> motherIdentity.getPregnancyGapCommaSeparatedValues() != null)
			.collect(toList());

		if (!allPregnantWomenToBeInformedOfGapInTheirPregnancy.isEmpty()) {
			broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy, "mother_identity");
		}
	}

	private void processRowsFromMotherEdit() throws IOException, InterruptedException {
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherEditRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(lastIdProperties.getAncVisitPregnancyGapLastId())
			.parallelStream().filter(motherEdit -> motherEdit.getPregnancyGapCommaSeparatedValues() != null)
			.collect(toList());

		if (!allPregnantWomenToBeInformedOfGapInTheirPregnancy.isEmpty()) {
			broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy, "mother_edit");
		}
	}

	private void broadcastPregnancyGapMessageTo(
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy, String fromTable)
		throws IOException, InterruptedException {

		// Create contact list CSV
		String contactsCsvFileName = qontakProperties.getWhatsApp().getPregnancyGapContactListCsvAbsoluteFileName()
			.replace(".csv", "_" + fromTable + ".csv");
		CSVUtil.createContactListCSVFileForPregnancyGapMessage(allPregnantWomenToBeInformedOfGapInTheirPregnancy,
			contactsCsvFileName);

		String campaignName = ZonedDateTime.now() + " " + qontakProperties.getWhatsApp().getDistrictHealthOfficeName()
			+ ", " + fromTable + " (Pregnancy Gap)";
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
					broadcastBulk(fromTable, allPregnantWomenToBeInformedOfGapInTheirPregnancy, campaignName,
						contactListId);
					return;
				}

				Thread.sleep(schedulingProperties.getContactList().getDelayInMs());
			}
			log.error(
				"\"Inform Pregnancy Gap via WhatsApp\" failed due to Contact List not available after {} retries with interval {}ms",
				schedulingProperties.getContactList().getMaxNumberOfRetries(),
				schedulingProperties.getContactList().getDelayInMs());

		} else {
			log.error(
				"\"Inform Pregnancy Gap via WhatsApp\" for enrolled pregnant women failed due to error when POST contact list. ("
					+ fromTable + ")");
		}
	}

	private void broadcastBulk(String fromTable,
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy, String campaignName,
		String contactListId) {
		// Broadcast to contact_list
		boolean isSuccess = broadcastBulkMessageViaWhatsApp(
			qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId(), contactListId, campaignName);

		log.info("\"Inform Pregnancy Gap via WhatsApp\" for enrolled pregnant women completed. (" + fromTable + ")");

		if (isSuccess) {
			log.info(
				"{} enrolled pregnant women have been informed of the gap in their pregnancy via WhatsApp successfully as bulk broadcast request.",
				allPregnantWomenToBeInformedOfGapInTheirPregnancy.size());
			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId(),
				"pregnancy_gap", allPregnantWomenToBeInformedOfGapInTheirPregnancy.size(), 0);
		} else {
			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId(),
				"pregnancy_gap", 0, allPregnantWomenToBeInformedOfGapInTheirPregnancy.size());
		}
	}

	private boolean broadcastBulkMessageViaWhatsApp(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = createPregnancyGapMessageRequestBody(messageTemplateId, contactListId,
			campaignName);
		return broadcastMessageService.sendBroadcastRequestToQontakAPI(requestBody);
	}

	private BroadcastRequest createPregnancyGapMessageRequestBody(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(campaignName,
			messageTemplateId, contactListId);

		setParametersForPregnancyGapMessage(requestBody);
		return requestBody;
	}

	private void setParametersForPregnancyGapMessage(BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", FULL_NAME);
		parameters.addBodyWithValues("2", ANC_DATE);
		parameters.addBodyWithValues("3", GESTATIONAL_AGE);
		parameters.addBodyWithValues("4", HEIGHT_IN_CM);
		parameters.addBodyWithValues("5", WEIGHT_IN_KG);
		parameters.addBodyWithValues("6", MUAC_IN_CM);
		parameters.addBodyWithValues("7", SYSTOLIC_BP);
		parameters.addBodyWithValues("8", DIASTOLIC_BP);
		parameters.addBodyWithValues("9", UTERINE_F_HEIGHT);
		parameters.addBodyWithValues("10", FETAL_PRESENTATI);
		parameters.addBodyWithValues("11", FETAL_HEART_RATE);
		parameters.addBodyWithValues("12", TETANUS_T_IMM_ST);
		parameters.addBodyWithValues("13", GIVEN_TT_INJECTI);
		parameters.addBodyWithValues("14", GIVEN_IFA_TABLET);
		parameters.addBodyWithValues("15", HAS_PROTEINURIA);
		parameters.addBodyWithValues("16", HB_LEVEL_RESULT);
		parameters.addBodyWithValues("17", GLUCOSE_140_MGDL);
		parameters.addBodyWithValues("18", HAS_THALASEMIA);
		parameters.addBodyWithValues("19", HAS_SYPHILIS);
		parameters.addBodyWithValues("20", HAS_HBSAG);
		parameters.addBodyWithValues("21", HAS_HIV);
		requestBody.setParameters(parameters);
	}
}