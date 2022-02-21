package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import java.util.concurrent.atomic.AtomicLong;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.response.BroadcastDirectResponse;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.response.BroadcastResponse;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.sidindonesia.bidanreport.util.IndonesiaPhoneNumberUtil;
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
public class BroadcastMessageService {
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;

	public void sendBroadcastDirectRequestToQontakAPI(AtomicLong successCount,
		MotherIdentityWhatsAppProjection motherIdentity, BroadcastDirectRequest requestBody) {
		Mono<BroadcastDirectResponse> response = webClient.post().uri(qontakProperties.getApiPathBroadcastDirect())
			.bodyValue(requestBody).header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
			.bodyToMono(BroadcastDirectResponse.class).onErrorResume(WebClientResponseException.class,
				ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
					? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), BroadcastDirectResponse.class))
					: Mono.error(ex));

		BroadcastDirectResponse responseBody = response.block();
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
	}

	public boolean sendBroadcastRequestToQontakAPI(BroadcastRequest requestBody) {
		Mono<BroadcastResponse> response = webClient.post().uri(qontakProperties.getApiPathBroadcast())
			.bodyValue(requestBody).header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
			.bodyToMono(BroadcastResponse.class).onErrorResume(WebClientResponseException.class,
				ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
					? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), BroadcastResponse.class))
					: Mono.error(ex));

		BroadcastResponse responseBody = response.block();
		if (responseBody != null) {
			if ("success".equals(responseBody.getStatus())) {
				return true;
			} else {
				log.error("Request broadcast message failed for Contact List with ID: {}, with error details: {}",
					requestBody.getContact_list_id(), responseBody.getError());
			}
		} else {
			log.error("Request broadcast message failed with no content for Contact List with ID: {}",
				requestBody.getContact_list_id());
		}
		return false;
	}

	public BroadcastDirectRequest createBroadcastDirectRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastDirectRequest requestBody = new BroadcastDirectRequest();
		requestBody.setChannel_integration_id(qontakProperties.getWhatsApp().getChannelIntegrationId());
		requestBody.setMessage_template_id(messageTemplateId);
		requestBody.setTo_name(motherIdentity.getFullName() == null ? motherIdentity.getMobilePhoneNumber()
			: motherIdentity.getFullName());
		requestBody.setTo_number(IndonesiaPhoneNumberUtil.sanitize(motherIdentity.getMobilePhoneNumber()));
		return requestBody;
	}

	public BroadcastRequest createBroadcastRequestBody(String name, String messageTemplateId, String contactListId) {
		BroadcastRequest requestBody = new BroadcastRequest();
		requestBody.setName(name);
		requestBody.setMessage_template_id(messageTemplateId);
		requestBody.setContact_list_id(contactListId);
		requestBody.setChannel_integration_id(qontakProperties.getWhatsApp().getChannelIntegrationId());
		return requestBody;
	}
}
