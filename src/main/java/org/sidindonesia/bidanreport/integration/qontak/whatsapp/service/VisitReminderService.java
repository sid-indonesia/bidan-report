package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ParametersWithHeader;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.AncVisitReminderProjection;
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
public class VisitReminderService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;
	private final FhirResourceService fhirResourceService;

	@Scheduled(cron = "${scheduling.visit-reminder.cron}", zone = "${scheduling.visit-reminder.zone}")
	public void sendVisitRemindersToEnrolledMothers() {
		log.debug("Executing scheduled \"Send ANC Visit Reminder via WhatsApp\"...");
		log.debug("Send ANC visit reminder to all mothers with -"
			+ qontakProperties.getWhatsApp().getVisitReminderIntervalInDays() + " day(s) for the next ANC visit date");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() {
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherIdentityRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
	}

	private void processRowsFromMotherEdit() {
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherEditRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
	}

	private void broadcastANCVisitReminderMessageTo(
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit) {
		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			AtomicLong visitReminderSuccessCount = new AtomicLong();
			List<Pair<AncVisitReminderProjection, BroadcastDirectRequest>> pairs = allPregnantWomenToBeRemindedForTheNextANCVisit
				.parallelStream()
				.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null
					&& ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues() == null)
				.map(
					ancVisitReminderProjection -> Pair.of(ancVisitReminderProjection,
						createANCVisitReminderMessageRequestBody(ancVisitReminderProjection,
							qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(), false)))
				.collect(toList());

			pairs.parallelStream().forEach(pair -> broadcastMessageService
				.sendBroadcastDirectRequestToQontakAPI(visitReminderSuccessCount, pair.getFirst(), pair.getSecond()));

			// cannot do parallel because QR code file generation
			allPregnantWomenToBeRemindedForTheNextANCVisit.stream()
				.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null
					&& ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues() != null)
				.forEach(ancVisitReminderProjection -> broadcastMessageService.sendBroadcastDirectRequestToQontakAPI(
					visitReminderSuccessCount, ancVisitReminderProjection,
					createANCVisitReminderMessageRequestBody(ancVisitReminderProjection,
						qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(), true)));

			log.info("\"Send ANC Visit Reminder via WhatsApp\" for enrolled pregnant women completed.");
			log.info(
				"{} out of {} enrolled pregnant women have been reminded of the next ANC visit via WhatsApp successfully.",
				visitReminderSuccessCount, allPregnantWomenToBeRemindedForTheNextANCVisit.size());

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(),
				"anc_visit_reminder", visitReminderSuccessCount.get(),
				allPregnantWomenToBeRemindedForTheNextANCVisit.size() - visitReminderSuccessCount.get());
		}
	}

	public BroadcastDirectRequest createANCVisitReminderMessageRequestBody(
		AncVisitReminderProjection ancVisitReminderProjection, String messageTemplateId, boolean useQRCodeAsHeader) {
		BroadcastDirectRequest requestBody = broadcastMessageService
			.createBroadcastDirectRequestBody(ancVisitReminderProjection, messageTemplateId);

		setParametersForANCVisitReminderMessage(ancVisitReminderProjection, requestBody, useQRCodeAsHeader);
		return requestBody;
	}

	private void setParametersForANCVisitReminderMessage(AncVisitReminderProjection ancVisitReminderProjection,
		BroadcastDirectRequest requestBody, boolean useQRCodeAsHeader) {
		Parameters parameters;
		if (useQRCodeAsHeader) {
			parameters = new ParametersWithHeader();
			fhirResourceService.fillHeaderWithQRCodeImage(ancVisitReminderProjection,
				Stream.of(ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues().split(",")).map(String::trim)
					.collect(toList()),
				(ParametersWithHeader) parameters);
			requestBody.setMessage_template_id(
				qontakProperties.getWhatsApp().getVisitReminderWithHeaderImageMessageTemplateId());
		} else {
			parameters = new Parameters();
		}
		fillParameters(ancVisitReminderProjection, requestBody, parameters);
	}

	private void fillParameters(AncVisitReminderProjection ancVisitReminderProjection,
		BroadcastDirectRequest requestBody, Parameters parameters) {
		parameters.addBodyWithValues("1", "full_name", ancVisitReminderProjection.getFullName());
		parameters.addBodyWithValues("2", "visit_number",
			String.valueOf(ancVisitReminderProjection.getLatestAncVisitNumber() + 1));
		requestBody.setParameters(parameters);
	}
}