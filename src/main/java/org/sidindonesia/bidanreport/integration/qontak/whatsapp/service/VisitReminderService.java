package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.AncVisitRepository;
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
	private final AncVisitRepository ancVisitRepository;
	private final BroadcastMessageService broadcastMessageService;

	@Scheduled(cron = "${scheduling.visit-reminder.cron}", zone = "${scheduling.visit-reminder.zone}", initialDelayString = "${scheduling.visit-reminder.initial-delay-in-ms}")
	public void sendVisitRemindersToEnrolledMothers() {
		log.debug("Executing scheduled \"Send Visit Reminder via WhatsApp\"...");
		log.debug("Retrieving all mothers with -3days ANC visit date timestamp");
		log.debug("Send visit reminder message to all retrieved mothers");
	}
}