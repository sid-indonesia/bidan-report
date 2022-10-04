package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.request.AuthRequest;
import org.sidindonesia.bidanreport.integration.qontak.web.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Integration tests for {@link IntroMessageService}.
 */
@IntegrationTest
@Transactional
class IntroMessageServiceTest {

	private static final String QONTAK_MOCK_SERVER_BASE_URL = "https://stoplight.io/mocks/qontak/omnichannel-hub";
	private WebClient webClient = WebClient.create(QONTAK_MOCK_SERVER_BASE_URL);

	@Autowired
	private QontakProperties qontakProperties;
	@Autowired
	private IntroMessageService whatsAppMessageService;
	@Autowired
	private DummyDataService dummyDataService;

	@Test
	void assertThatQontakWhatsAppConfigPropertiesAreCorrect() {
		assertThat(qontakProperties.getUsername()).isEqualTo("test");
		assertThat(qontakProperties.getPassword()).isEqualTo("password");
	}

	@Test
	void testAuthenticationAtMockServer() {
		AuthRequest requestBody = new AuthRequest();
		requestBody.setClient_id(qontakProperties.getClientId());
		requestBody.setClient_secret(qontakProperties.getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getUsername());
		requestBody.setPassword(qontakProperties.getPassword());
		Mono<AuthResponse> response = webClient.post().uri(qontakProperties.getApiPathAuthentication())
			.bodyValue(requestBody).retrieve().bodyToMono(AuthResponse.class);

		AuthResponse responseBody = response.block();
		assertThat(responseBody.getAccess_token()).isNotBlank();
	}

	@Test
	void testSendWhatsAppMessageToNewMothers_withHappyFlow() throws Exception {
		// given
		dummyDataService.insertDummyData();
		// when
		whatsAppMessageService.sendIntroMessageToNewMothersViaWhatsApp();
		// then
		assertThat(whatsAppMessageService).isNotNull();
	}

	@Test
	void testSendWhatsAppMessageToNewMothers_withNoNewMothers() throws Exception {
		assertThat(whatsAppMessageService).isNotNull();
		whatsAppMessageService.sendIntroMessageToNewMothersViaWhatsApp();
	}
}
