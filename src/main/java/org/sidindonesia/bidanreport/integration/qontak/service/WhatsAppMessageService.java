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
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.google.gson.Gson;

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
	private final Gson gson;

	@Scheduled(fixedRateString = "${scheduling.fixed-rate-in-ms}", initialDelayString = "${scheduling.initial-delay-in-ms}")
	public void sendWhatsAppMessageToNewMothers() {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\"...");

		log.debug("Retrieving all new mother identities...");
		List<MotherIdentityWhatsAppProjection> motherIdentities = motherIdentityRepository
			.findAllByEventIdGreaterThanAndMobilePhoneNumberIsNotNullAndProviderIdNotContainingDemoOrderByEventId(
				motherIdentityProperties.getLastId());

		AtomicLong successCount = new AtomicLong();
		motherIdentities.parallelStream().forEach(motherIdentity -> {
			QontakWhatsAppBroadcastRequest requestBody = createBroadcastDirectRequestBody(motherIdentity);
			Mono<QontakWhatsAppBroadcastResponse> response = webClient.post()
				.uri(qontakProperties.getWhatsApp().getApiPathBroadcastDirect()).bodyValue(requestBody)
				.header("Authorization", "Bearer " + qontakProperties.getWhatsApp().getAccessToken()).retrieve()
				.bodyToMono(QontakWhatsAppBroadcastResponse.class).onErrorResume(WebClientResponseException.class,
					ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
						? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), QontakWhatsAppBroadcastResponse.class))
						: Mono.error(ex));

			QontakWhatsAppBroadcastResponse responseBody = response.block();
			if (responseBody != null) {
				if ("success".equals(responseBody.getStatus())) {
					successCount.incrementAndGet();
				} else {
					log.error(
						"Request broadcast direct message failed for: {}, at phone number: {}, with error details: {}",
						motherIdentity.getFullName(), motherIdentity.getMobilePhoneNumber(), responseBody.getError());
				}
			} else {
				log.error("Request broadcast direct message failed with no content for: {}, at phone number: {}",
					motherIdentity.getFullName(), motherIdentity.getMobilePhoneNumber());
			}
		});

		if (!motherIdentities.isEmpty()) {
			motherIdentityProperties.setLastId(motherIdentities.get(motherIdentities.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" completed.");
			log.info("{} out of {} new enrolled mothers have been notified via WhatsApp successfully.", successCount,
				motherIdentities.size());
		}
	}

	private QontakWhatsAppBroadcastRequest createBroadcastDirectRequestBody(
		MotherIdentityWhatsAppProjection motherIdentity) {
		QontakWhatsAppBroadcastRequest requestBody = new QontakWhatsAppBroadcastRequest();
		requestBody.setChannel_integration_id(qontakProperties.getWhatsApp().getChannelIntegrationId());
		requestBody.setMessage_template_id(qontakProperties.getWhatsApp().getMessageTemplateId());
		requestBody.setTo_name(motherIdentity.getFullName());
		requestBody.setTo_number(IndonesiaPhoneNumberUtil.sanitize(motherIdentity.getMobilePhoneNumber()));

		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "dho", qontakProperties.getWhatsApp().getDistrictHealthOfficeName());

		requestBody.setParameters(parameters);
		return requestBody;
	}
}