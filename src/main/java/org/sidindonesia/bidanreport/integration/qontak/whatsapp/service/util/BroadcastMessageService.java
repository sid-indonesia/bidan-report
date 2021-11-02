package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest.Parameters;
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

	public Consumer<MotherIdentityWhatsAppProjection> broadcastDirectMessageViaWhatsApp(AtomicLong successCount,
		String messageTemplateId) {
		return motherIdentity -> {
			BroadcastRequest requestBody = createBroadcastDirectRequestBody(motherIdentity, messageTemplateId);
			Mono<BroadcastResponse> response = webClient.post()
				.uri(qontakProperties.getWhatsApp().getApiPathBroadcastDirect()).bodyValue(requestBody)
				.header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
				.bodyToMono(BroadcastResponse.class).onErrorResume(WebClientResponseException.class,
					ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
						? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), BroadcastResponse.class))
						: Mono.error(ex));

			BroadcastResponse responseBody = response.block();
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
		};
	}

	private BroadcastRequest createBroadcastDirectRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastRequest requestBody = new BroadcastRequest();
		requestBody.setChannel_integration_id(qontakProperties.getWhatsApp().getChannelIntegrationId());
		requestBody.setMessage_template_id(messageTemplateId);
		requestBody.setTo_name(motherIdentity.getFullName());
		requestBody.setTo_number(IndonesiaPhoneNumberUtil.sanitize(motherIdentity.getMobilePhoneNumber()));

		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "dho", qontakProperties.getWhatsApp().getDistrictHealthOfficeName());

		requestBody.setParameters(parameters);
		return requestBody;
	}
}
