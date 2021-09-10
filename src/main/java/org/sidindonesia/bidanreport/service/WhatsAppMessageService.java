package org.sidindonesia.bidanreport.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class WhatsAppMessageService {
	@Scheduled
	public void sendWhatsAppMessageToNewClients() {

	}
}
