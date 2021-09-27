package org.sidindonesia.bidanreport.integration.qontak.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.sidindonesia.bidanreport.config.property.MotherIdentityProperties;
import org.sidindonesia.bidanreport.integration.qontak.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppBroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppBroadcastRequest.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.response.QontakWhatsAppBroadcastResponse;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.sidindonesia.bidanreport.util.IndonesiaPhoneNumberUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class WhatsAppMessageService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherIdentityProperties motherIdentityProperties;
	private final QontakProperties qontakProperties;
	private final WebClient webClient;

	@Scheduled(fixedRateString = "${scheduling.fixed-rate-in-ms}")
	public void sendWhatsAppMessageToNewMothers() {
		log.info("Executing scheduled \"Send Join Notification via WhatsApp\"...");

		log.debug("Retrieving all new mother identities...");
		List<MotherIdentityWhatsAppProjection> motherIdentities = motherIdentityRepository
			.findAllByEventIdGreaterThanAndMobilePhoneNumberIsNotNullAndProviderIdNotContainingDemoOrderByEventId(
				motherIdentityProperties.getLastId());

		AtomicLong successCount = new AtomicLong();
		motherIdentities.forEach(motherIdentity -> {
			QontakWhatsAppBroadcastRequest requestBody = new QontakWhatsAppBroadcastRequest();
			requestBody.setChannel_integration_id(qontakProperties.getWhatsApp().getChannelIntegrationId());
			requestBody.setMessage_template_id(qontakProperties.getWhatsApp().getMessageTemplateId());
			requestBody.setTo_name(motherIdentity.getFullName());
			requestBody.setTo_number(IndonesiaPhoneNumberUtil.sanitize(motherIdentity.getMobilePhoneNumber()));

			Parameters parameters = new Parameters();
			parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
			parameters.addBodyWithValues("2", "dho", qontakProperties.getWhatsApp().getDistrictHealthOfficeName());

			requestBody.setParameters(parameters);
			Mono<QontakWhatsAppBroadcastResponse> response = webClient.post()
				.uri("/api/open/v1/broadcasts/whatsapp/direct").bodyValue(requestBody)
				.header("Authorization", "Bearer " + qontakProperties.getWhatsApp().getAccessToken()).retrieve()
				.bodyToMono(QontakWhatsAppBroadcastResponse.class);

			if ("success".equals(response.block().getStatus())) {
				successCount.incrementAndGet();
			}
		});

		motherIdentityProperties.setLastId(motherIdentities.get(motherIdentities.size() - 1).getEventId());
		log.info("\"Send Join Notification via WhatsApp\" completed.");
		log.info("{} out of {} new enrolled mothers have been notified via WhatsApp successfully.", successCount,
			motherIdentities.size());
	}
}