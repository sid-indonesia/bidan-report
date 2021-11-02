package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class VisitReminderService {
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;

	@Scheduled(fixedRateString = "${scheduling.fixed-rate-in-ms}", initialDelayString = "${scheduling.initial-delay-in-ms}")
	public void sendVisitRemindersToEnrolledMothers() {
		log.debug("Executing scheduled \"Send Visit Reminder via WhatsApp\"...");
	}
}