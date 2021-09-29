package org.sidindonesia.bidanreport.integration.qontak.listener;

import java.util.Optional;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.domain.MotherEdit;
import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.integration.qontak.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppAuthRequest;
import org.sidindonesia.bidanreport.integration.qontak.response.QontakWhatsAppAuthResponse;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class QontakAuthenticationListener implements ApplicationListener<ApplicationReadyEvent> {

	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("Authenticating to {}", qontakProperties.getWhatsApp().getBaseUrl());

		QontakWhatsAppAuthRequest requestBody = createQontakAuthRequestBody();
		Mono<QontakWhatsAppAuthResponse> response = webClient.post()
			.uri(qontakProperties.getWhatsApp().getApiPathAuthentication()).bodyValue(requestBody).retrieve()
			.bodyToMono(QontakWhatsAppAuthResponse.class);

		QontakWhatsAppAuthResponse responseBody = response.block();
		if (responseBody != null) {
			qontakProperties.getWhatsApp().setAccessToken(responseBody.getAccess_token());
			qontakProperties.getWhatsApp().setRefreshToken(responseBody.getRefresh_token());
			qontakProperties.getWhatsApp().setTokenType(responseBody.getToken_type());
		} else {
			log.error("Failed to authenticate to {}", qontakProperties.getWhatsApp().getBaseUrl());
		}
		log.info("Authenticated to {} successfully.", qontakProperties.getWhatsApp().getBaseUrl());
		syncLastId();
	}

	private QontakWhatsAppAuthRequest createQontakAuthRequestBody() {
		QontakWhatsAppAuthRequest requestBody = new QontakWhatsAppAuthRequest();
		requestBody.setClient_id(qontakProperties.getWhatsApp().getClientId());
		requestBody.setClient_secret(qontakProperties.getWhatsApp().getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getWhatsApp().getUsername());
		requestBody.setPassword(qontakProperties.getWhatsApp().getPassword());
		return requestBody;
	}

	private void syncLastId() {
		Optional<MotherIdentity> optMotherIdentity = motherIdentityRepository.findFirstByOrderByEventIdDesc();
		if (optMotherIdentity.isPresent()) {
			lastIdProperties.setMotherIdentityLastId(optMotherIdentity.get().getEventId());
		}
		Optional<MotherEdit> optMotherEdit = motherEditRepository.findFirstByOrderByEventIdDesc();
		if (optMotherEdit.isPresent()) {
			lastIdProperties.setMotherEditLastId(optMotherEdit.get().getEventId());
		}
		log.info("Sync-ed last ID from DB successfully.");
	}
}
