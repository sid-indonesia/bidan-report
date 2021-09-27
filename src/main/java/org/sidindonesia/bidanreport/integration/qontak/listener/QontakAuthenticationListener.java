package org.sidindonesia.bidanreport.integration.qontak.listener;

import org.sidindonesia.bidanreport.integration.qontak.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppAuthRequest;
import org.sidindonesia.bidanreport.integration.qontak.response.QontakWhatsAppAuthResponse;
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

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("Authenticating to {}", qontakProperties.getWhatsApp().getBaseUrl());

		QontakWhatsAppAuthRequest requestBody = new QontakWhatsAppAuthRequest();
		requestBody.setClient_id(qontakProperties.getWhatsApp().getClientId());
		requestBody.setClient_secret(qontakProperties.getWhatsApp().getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getWhatsApp().getUsername());
		requestBody.setPassword(qontakProperties.getWhatsApp().getPassword());
		Mono<QontakWhatsAppAuthResponse> response = webClient.post().uri("/oauth/token").bodyValue(requestBody)
			.retrieve().bodyToMono(QontakWhatsAppAuthResponse.class);

		QontakWhatsAppAuthResponse responseBody = response.block();
		qontakProperties.getWhatsApp().setAccessToken(responseBody.getAccess_token());
		qontakProperties.getWhatsApp().setRefreshToken(responseBody.getRefresh_token());
		qontakProperties.getWhatsApp().setTokenType(responseBody.getToken_type());
		log.info("Authenticated to {} successfully.", qontakProperties.getWhatsApp().getBaseUrl());
		syncLastId();
	}

	private void syncLastId() {
	}
}
