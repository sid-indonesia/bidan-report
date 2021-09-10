package org.sidindonesia.bidanreport.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.config.property.QontakProperties;
import org.sidindonesia.bidanreport.controller.request.QontakWhatsAppAuthRequest;
import org.sidindonesia.bidanreport.controller.response.QontakWhatsAppAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Integration tests for {@link WhatsAppMessageService}.
 */
@IntegrationTest
@Transactional
class WhatsAppMessageServiceTest {

	private static final String QONTAK_MOCK_SERVER_BASE_URL = "https://stoplight.io/mocks/qontak/omnichannel-hub/17521989";
	private WebClient webClient = WebClient.create(QONTAK_MOCK_SERVER_BASE_URL);
	@Autowired
	private QontakProperties qontakProperties;

	@Test
	void assertThatQontakWhatsAppConfigPropertiesAreCorrect() {
		assertThat(qontakProperties.getWhatsApp().getBaseUrl()).isEqualTo("https://chat-service.qontak.com");

		// client-id and client-secret retrieved from
		// https://docs.qontak.com/docs/omnichannel-hub/ZG9jOjE1MzEwMzc4-authentication#how-to-get-token
		assertThat(qontakProperties.getWhatsApp().getClientId())
			.isEqualTo("RRrn6uIxalR_QaHFlcKOqbjHMG63elEdPTair9B9YdY");
		assertThat(qontakProperties.getWhatsApp().getClientSecret())
			.isEqualTo("Sa8IGIh_HpVK1ZLAF0iFf7jU760osaUNV659pBIZR00");

		assertThat(qontakProperties.getWhatsApp().getUsername()).isEqualTo("test");
		assertThat(qontakProperties.getWhatsApp().getPassword()).isEqualTo("password");
		assertThat(qontakProperties.getWhatsApp().getAccessToken()).isNull();
	}

	@Test
	void testAuthenticationAtMockServer() {
		QontakWhatsAppAuthRequest requestBody = new QontakWhatsAppAuthRequest();
		requestBody.setClient_id(qontakProperties.getWhatsApp().getClientId());
		requestBody.setClient_secret(qontakProperties.getWhatsApp().getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getWhatsApp().getUsername());
		requestBody.setPassword(qontakProperties.getWhatsApp().getPassword());
		Mono<QontakWhatsAppAuthResponse> response = webClient.post().uri("/oauth/token").bodyValue(requestBody)
			.retrieve().bodyToMono(QontakWhatsAppAuthResponse.class);

		QontakWhatsAppAuthResponse responseBody = response.block();
		assertThat(responseBody.getAccess_token()).isNotBlank();
	}
}
