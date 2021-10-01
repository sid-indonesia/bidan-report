package org.sidindonesia.bidanreport.integration.qontak.listener;

import java.util.Optional;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
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
			qontakProperties.setAccessToken(responseBody.getAccess_token());
			qontakProperties.setRefreshToken(responseBody.getRefresh_token());
			qontakProperties.setTokenType(responseBody.getToken_type());
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
		Optional<Long> optMotherIdentityLastId = motherIdentityRepository.findFirstPregnantWomanByOrderByEventIdDesc();
		if (optMotherIdentityLastId.isPresent()) {
			lastIdProperties.getMotherIdentity().setPregnantMotherLastId(optMotherIdentityLastId.get());
		}
		Optional<Long> optMotherEditLastId = motherEditRepository.findFirstPregnantWomanByOrderByEventIdDesc();
		if (optMotherEditLastId.isPresent()) {
			lastIdProperties.getMotherEdit().setPregnantMotherLastId(optMotherEditLastId.get());
		}
		log.info("Sync-ed last ID from DB successfully.");
	}
}
