package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
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
		log.debug("Executing scheduled \"Send Visit Reminder via WhatsApp\"...");
		log.debug("Send visit reminder to all mothers with -"
			+ qontakProperties.getWhatsApp().getVisitReminderIntervalInDays() + " days ANC visit date");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherIdentityRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
		}
	}

	private void processRowsFromMotherEdit() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherEditRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays());

		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit);
		}
	}

	private void broadcastANCVisitReminderMessageTo(
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeRemindedForTheNextANCVisit) {
		AtomicLong visitReminderSuccessCount = new AtomicLong();
		allPregnantWomenToBeRemindedForTheNextANCVisit.parallelStream()
			.forEach(broadcastMessageService.broadcastANCVisitReminderMessageViaWhatsApp(visitReminderSuccessCount,
				qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId()));
		log.info("\"Send Visit Reminder via WhatsApp\" for enrolled pregnant women completed.");
		log.info(
			"{} out of {} enrolled pregnant women have been reminded of the next ANC visit via WhatsApp successfully.",
			visitReminderSuccessCount, allPregnantWomenToBeRemindedForTheNextANCVisit.size());
	}
}