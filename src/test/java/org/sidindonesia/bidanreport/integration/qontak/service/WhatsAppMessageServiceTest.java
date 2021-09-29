package org.sidindonesia.bidanreport.integration.qontak.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppAuthRequest;
import org.sidindonesia.bidanreport.integration.qontak.response.QontakWhatsAppAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Integration tests for {@link WhatsAppMessageService}.
 */
@IntegrationTest
@Transactional
class WhatsAppMessageServiceTest {

	private static final String QONTAK_MOCK_SERVER_BASE_URL = "https://stoplight.io/mocks/qontak/omnichannel-hub";
	private WebClient webClient = WebClient.create(QONTAK_MOCK_SERVER_BASE_URL);

	@Autowired
	private QontakProperties qontakProperties;
	@Autowired
	private WhatsAppMessageService whatsAppMessageService;
	@Autowired
	private JdbcOperations jdbcOperations;

	void insertDummyData() {
		IntStream.rangeClosed(1, 2).forEach(insertIntoClientMother());
		IntStream.rangeClosed(1, 3).forEach(insertIntoMotherIdentity());

		IntStream.rangeClosed(4, 5).forEach(insertIntoClientMother());
		IntStream.rangeClosed(4, 6).forEach(insertIntoMotherIdentityWithoutMobilePhoneNumber());
		IntStream.rangeClosed(7, 9).forEach(insertIntoMotherEdit());
	}

	private IntConsumer insertIntoClientMother() {
		return id -> {
			jdbcOperations.execute("INSERT INTO client_mother\n"
				+ "(source_id, date_created, base_entity_id, birth_date, server_version_epoch, source_date_deleted, full_name)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, '" + id + "', CURRENT_TIMESTAMP, '157663657225" + id
				+ "', CURRENT_TIMESTAMP, 'Test " + id + "');\n");
		};
	}

	private IntConsumer insertIntoMotherIdentity() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_identity\n"
				+ "(event_id, date_created, event_date, mobile_phone_number, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '08381234567" + id + "', '" + id
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	private IntConsumer insertIntoMotherIdentityWithoutMobilePhoneNumber() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_identity\n"
				+ "(event_id, date_created, event_date, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '" + id
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	private IntConsumer insertIntoMotherEdit() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_edit\n"
				+ "(event_id, date_created, event_date, mobile_phone_number, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '08381234567" + id + "', '" + (id - 3)
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	@Test
	void assertThatQontakWhatsAppConfigPropertiesAreCorrect() {
		// client-id and client-secret retrieved from
		// https://docs.qontak.com/docs/omnichannel-hub/ZG9jOjE1MzEwMzc4-authentication#how-to-get-token
		assertThat(qontakProperties.getWhatsApp().getClientId())
			.isEqualTo("RRrn6uIxalR_QaHFlcKOqbjHMG63elEdPTair9B9YdY");
		assertThat(qontakProperties.getWhatsApp().getClientSecret())
			.isEqualTo("Sa8IGIh_HpVK1ZLAF0iFf7jU760osaUNV659pBIZR00");

		assertThat(qontakProperties.getWhatsApp().getUsername()).isEqualTo("test");
		assertThat(qontakProperties.getWhatsApp().getPassword()).isEqualTo("password");
	}

	@Test
	void testAuthenticationAtMockServer() {
		QontakWhatsAppAuthRequest requestBody = new QontakWhatsAppAuthRequest();
		requestBody.setClient_id(qontakProperties.getWhatsApp().getClientId());
		requestBody.setClient_secret(qontakProperties.getWhatsApp().getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getWhatsApp().getUsername());
		requestBody.setPassword(qontakProperties.getWhatsApp().getPassword());
		Mono<QontakWhatsAppAuthResponse> response = webClient.post().uri("/17521989/oauth/token").bodyValue(requestBody)
			.retrieve().bodyToMono(QontakWhatsAppAuthResponse.class);

		QontakWhatsAppAuthResponse responseBody = response.block();
		assertThat(responseBody.getAccess_token()).isNotBlank();
	}

	@Test
	void testSendWhatsAppMessageToNewMothers_withHappyFlow() {
		insertDummyData();
		assertThat(whatsAppMessageService).isNotNull();
		whatsAppMessageService.sendWhatsAppMessageToNewMothers();
	}

	@Test
	void testSendWhatsAppMessageToNewMothers_withNoNewMothers() {
		assertThat(whatsAppMessageService).isNotNull();
		whatsAppMessageService.sendWhatsAppMessageToNewMothers();
	}
}
