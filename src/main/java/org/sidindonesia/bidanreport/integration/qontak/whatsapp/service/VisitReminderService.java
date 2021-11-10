package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class VisitReminderService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;

	@Scheduled(cron = "${scheduling.visit-reminder.cron}", zone = "${scheduling.visit-reminder.zone}")
	public void sendVisitRemindersToEnrolledMothers() {
		log.debug("Executing scheduled \"Send ANC Visit Reminder via WhatsApp\"...");
		log.debug("Send ANC visit reminder to all mothers with -"
			+ qontakProperties.getWhatsApp().getVisitReminderIntervalInDays() + " days ANC visit date");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherIdentityRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
	}

	private void processRowsFromMotherEdit() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherEditRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
	}

	private void broadcastANCVisitReminderMessageTo(
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit) {
		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			AtomicLong visitReminderSuccessCount = new AtomicLong();
			allPregnantWomenToBeRemindedForTheNextANCVisit.parallelStream()
				.forEach(broadcastANCVisitReminderMessageViaWhatsApp(visitReminderSuccessCount,
					qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId()));
			log.info("\"Send ANC Visit Reminder via WhatsApp\" for enrolled pregnant women completed.");
			log.info(
				"{} out of {} enrolled pregnant women have been reminded of the next ANC visit via WhatsApp successfully.",
				visitReminderSuccessCount, allPregnantWomenToBeRemindedForTheNextANCVisit.size());
		}
	}

	private Consumer<MotherIdentityWhatsAppProjection> broadcastANCVisitReminderMessageViaWhatsApp(
		AtomicLong successCount, String messageTemplateId) {
		return motherIdentity -> {
			BroadcastRequest requestBody = createANCVisitReminderMessageRequestBody(motherIdentity, messageTemplateId);
			broadcastMessageService.sendBroadcastRequestToQontakAPI(successCount, motherIdentity, requestBody);
		};
	}

	private BroadcastRequest createANCVisitReminderMessageRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(motherIdentity,
			messageTemplateId);

		setParametersForANCVisitReminderMessage(motherIdentity, requestBody);
		return requestBody;
	}

	private void setParametersForANCVisitReminderMessage(MotherIdentityWhatsAppProjection motherIdentity,
		BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "date",
			LocalDate.now().plusDays(qontakProperties.getWhatsApp().getVisitReminderIntervalInDays()).toString());
		requestBody.setParameters(parameters);
	}
}