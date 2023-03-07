package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import org.sidindonesia.bidanreport.config.property.SchedulingProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.constant.Constants;
import org.sidindonesia.bidanreport.integration.qontak.web.response.RetrieveContactListResponse;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ContactListRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.response.CreateContactListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
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
	private final SchedulingProperties schedulingProperties;

	public String sendCreateContactListRequestToQontakAPI(ContactListRequest requestBody) throws InterruptedException {
		Mono<CreateContactListResponse> response = webClient.post().uri(qontakProperties.getApiPathContactListAsync())
		    .body(BodyInserters.fromMultipartData("file", requestBody.getFile()).with("name", requestBody.getName())
		        .with("source_type", requestBody.getSource_type()))
		    .header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
		    .bodyToMono(CreateContactListResponse.class).onErrorResume(WebClientResponseException.class, ex -> {
			    if (ex.getRawStatusCode() == 429) {
				    return Mono.just(new CreateContactListResponse().withStatus(ex.getRawStatusCode()));
			    }
			    return ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
			        ? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), CreateContactListResponse.class))
			        : Mono.error(ex);
		    });

		for (int i = 0; i < schedulingProperties.getContactList().getMaxNumberOfRetries(); i++) {
			CreateContactListResponse responseBody = response.block();
			if (responseBody != null) {
				if ("429".equalsIgnoreCase(responseBody.getStatus())) {
					Thread.sleep(schedulingProperties.getContactList().getDelayInMs());
					log.debug("Retrying create Contact List: {}", requestBody);
				} else if (Constants.SUCCESS.equals(responseBody.getStatus())) {
					return responseBody.getData().getId();
				} else {
					log.error("Request create contact list failed with error details: {}", responseBody.getError());
				}
			} else {
				log.error("Request create contact list failed with no content.");
			}
		}
		return null;
	}

	public RetrieveContactListResponse retrieveContactListRequestToQontakAPI(String contactListId) {
		Mono<RetrieveContactListResponse> response = webClient.get()
		    .uri(qontakProperties.getApiPathContactList() + "/" + contactListId)
		    .header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
		    .bodyToMono(RetrieveContactListResponse.class).onErrorResume(WebClientResponseException.class,
		        ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
		            ? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), RetrieveContactListResponse.class))
		            : Mono.error(ex));

		RetrieveContactListResponse responseBody = response.block();
		if (responseBody != null) {
			if (Constants.SUCCESS.equals(responseBody.getStatus())) {
				return responseBody;
			} else {
				log.error("Request retrieve contact list failed with error details: {}", responseBody.getError());
			}
		} else {
			log.error("Request retrieve contact list failed with no content.");
		}
		return null;
	}

	public boolean tryRetrieveContactListByIdMultipleTimes(String contactListId) throws InterruptedException {
		// Give Qontak some time to process the contact list (because the API is
		// asynchronous and currently there is no "Create contact list synchronously")
		Thread.sleep(schedulingProperties.getContactList().getInitialDelayInMs()); // give initial 5 seconds
		for (int i = 0; i < schedulingProperties.getContactList().getMaxNumberOfRetries(); i++) {
			RetrieveContactListResponse response = retrieveContactListRequestToQontakAPI(contactListId);

			if (response != null && response.getData() != null
			    && Constants.SUCCESS.equalsIgnoreCase(response.getData().getProgress())) {
				return true;
			}

			Thread.sleep(schedulingProperties.getContactList().getRetrieveDelayInMs());
		}
		log.error("Bulk broadcast request failed due to Contact List not available after {} retries with interval {}ms",
		    schedulingProperties.getContactList().getMaxNumberOfRetries(),
		    schedulingProperties.getContactList().getRetrieveDelayInMs());
		return false;
	}
}
