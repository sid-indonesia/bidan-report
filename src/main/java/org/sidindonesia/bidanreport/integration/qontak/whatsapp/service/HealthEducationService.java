package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;
import static org.sidindonesia.bidanreport.util.CSVUtil.FULL_NAME;
import static org.sidindonesia.bidanreport.util.CSVUtil.PREGNA_TRIMESTER;
import static org.sidindonesia.bidanreport.util.CSVUtil.CALC_GESTATIONAL;

import java.io.IOException;
import java.util.List;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ContactListRequest;
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
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;

	@Scheduled(cron = "${scheduling.health-education.cron}", zone = "${scheduling.health-education.zone}")
	public void sendHealthEducationsToEnrolledMothers() throws IOException {
		log.debug("Executing scheduled \"Send Health Education via WhatsApp\"...");
		log.debug("Send scheduled health education messages to all pregnant mothers "
			+ "with current_date between last_menstrual_period_date and expected_delivery_date "
			+ "and not recorded in `anc_close` within that period.");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() throws IOException {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherIdentityRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage);
	}

	private void processRowsFromMotherEdit() throws IOException {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherEditRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage);
	}

	private void broadcastHealthEducationMessageTo(
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage) throws IOException {
		if (!allPregnantWomenToBeGivenHealthEducationMessage.isEmpty()) {
			List<HealthEducationProjection> filteredPregnantWomen = allPregnantWomenToBeGivenHealthEducationMessage
				.parallelStream()
				.filter(healthEducationProjection -> healthEducationProjection.getCalculatedGestationalAge() != null
					&& healthEducationProjection.getPregnancyTrimester() != null)
				.collect(toList());
			// Create contact list CSV
			CSVUtil.createContactListCSVFile(filteredPregnantWomen,
				qontakProperties.getWhatsApp().getHealthEducationContactListCsvAbsoluteFileName());

			// Hit API post contact list, get contact_list_id
			String contactListId = contactListService.sendCreateContactListRequestToQontakAPI(new ContactListRequest());

			// Broadcast to contact_list
			boolean isSuccess = broadcastHealthEducationMessageViaWhatsApp(
				qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId(), contactListId);

			log.info("\"Send Health Education via WhatsApp\" for enrolled pregnant women completed.");

			if (isSuccess) {
				log.info(
					"{} enrolled pregnant women have been given health education via WhatsApp successfully as bulk broadcast request.",
					filteredPregnantWomen.size());
				automatedMessageStatsRepository.upsert(
					qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId(), "health_education",
					filteredPregnantWomen.size(), 0);
			} else {
				automatedMessageStatsRepository.upsert(
					qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId(), "health_education", 0,
					filteredPregnantWomen.size());
			}
		}
	}

	private boolean broadcastHealthEducationMessageViaWhatsApp(String messageTemplateId, String contactListId) {
		BroadcastRequest requestBody = createHealthEducationMessageRequestBody(messageTemplateId, contactListId);
		return broadcastMessageService.sendBroadcastRequestToQontakAPI(requestBody);
	}

	private BroadcastRequest createHealthEducationMessageRequestBody(String messageTemplateId, String contactListId) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody("Weekly Health Education",
			messageTemplateId, contactListId);

		setParametersForHealthEducationMessage(requestBody);
		return requestBody;
	}

	private void setParametersForHealthEducationMessage(BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", FULL_NAME);
		parameters.addBodyWithValues("2", PREGNA_TRIMESTER);
		parameters.addBodyWithValues("3", CALC_GESTATIONAL);
		requestBody.setParameters(parameters);
	}
}