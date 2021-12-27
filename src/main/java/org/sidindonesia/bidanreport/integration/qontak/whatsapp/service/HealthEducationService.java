package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.HealthEducationProjection;
import org.springframework.data.util.Pair;
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
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;

	@Scheduled(cron = "${scheduling.health-education.cron}", zone = "${scheduling.health-education.zone}")
	public void sendHealthEducationsToEnrolledMothers() {
		log.debug("Executing scheduled \"Send Health Education via WhatsApp\"...");
		log.debug("Send scheduled health education messages to all pregnant mothers "
			+ "with current_date between last_menstrual_period_date and expected_delivery_date "
			+ "and not recorded in `anc_close` within that period.");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherIdentityRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage);
	}

	private void processRowsFromMotherEdit() {
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage = motherEditRepository
			.findAllPregnantWomenToBeGivenEducationOfTheirHealth();

		broadcastHealthEducationMessageTo(allPregnantWomenToBeGivenHealthEducationMessage);
	}

	private void broadcastHealthEducationMessageTo(
		List<HealthEducationProjection> allPregnantWomenToBeGivenHealthEducationMessage) {
		if (!allPregnantWomenToBeGivenHealthEducationMessage.isEmpty()) {
			AtomicLong healthEducationSuccessCount = new AtomicLong();
			List<Pair<HealthEducationProjection, BroadcastRequest>> pairs = allPregnantWomenToBeGivenHealthEducationMessage
				.parallelStream()
				.filter(healthEducationProjection -> healthEducationProjection.getCalculatedGestationalAge() != null
					&& healthEducationProjection.getPregnancyTrimester() != null)
				.map(healthEducationProjection -> createHealthEducationMessageRequestBody(healthEducationProjection,
					qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId()))
				.collect(toList());

			pairs.parallelStream().forEach(pair -> broadcastMessageService
				.sendBroadcastRequestToQontakAPI(healthEducationSuccessCount, pair.getFirst(), pair.getSecond()));

			log.info("\"Send Health Education via WhatsApp\" for enrolled pregnant women completed.");
			log.info("{} out of {} enrolled pregnant women have been given health education via WhatsApp successfully.",
				healthEducationSuccessCount, allPregnantWomenToBeGivenHealthEducationMessage.size());

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getHealthEducationMessageTemplateId(),
				"health_education", healthEducationSuccessCount.get(),
				allPregnantWomenToBeGivenHealthEducationMessage.size() - healthEducationSuccessCount.get());
		}
	}

	private Pair<HealthEducationProjection, BroadcastRequest> createHealthEducationMessageRequestBody(
		HealthEducationProjection healthEducationProjection, String messageTemplateId) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(healthEducationProjection,
			messageTemplateId);

		setParametersForHealthEducationMessage(healthEducationProjection, requestBody);
		return Pair.of(healthEducationProjection, requestBody);
	}

	private void setParametersForHealthEducationMessage(HealthEducationProjection healthEducationProjection,
		BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", healthEducationProjection.getFullName());
		parameters.addBodyWithValues("2", "pregna_trimester", healthEducationProjection.getPregnancyTrimester());
		parameters.addBodyWithValues("3", "calc_gestational", healthEducationProjection.getCalculatedGestationalAge());
		requestBody.setParameters(parameters);
	}
}