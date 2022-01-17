package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ContactListRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.response.CreateContactListResponse;
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
public class ContactListService {
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;

	public String sendCreateContactListRequestToQontakAPI(ContactListRequest requestBody) {
		Mono<CreateContactListResponse> response = webClient.post().uri(qontakProperties.getApiPathContactListAsync())
			.bodyValue(requestBody).header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
			.bodyToMono(CreateContactListResponse.class).onErrorResume(WebClientResponseException.class,
				ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
					? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), CreateContactListResponse.class))
					: Mono.error(ex));

		CreateContactListResponse responseBody = response.block();
		if (responseBody != null) {
			if ("success".equals(responseBody.getStatus())) {
				return responseBody.getData().getId();
			} else {
				log.error("Request create contact list failed with error details: {}", responseBody.getError());
			}
		} else {
			log.error("Request create contact list failed with no content.");
		}
		return null;
	}
}
