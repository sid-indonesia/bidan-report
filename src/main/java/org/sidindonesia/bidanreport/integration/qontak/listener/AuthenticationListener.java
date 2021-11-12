package org.sidindonesia.bidanreport.integration.qontak.listener;

import java.util.Optional;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.request.AuthRequest;
import org.sidindonesia.bidanreport.integration.qontak.web.response.AuthResponse;
import org.sidindonesia.bidanreport.repository.AncVisitRepository;
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
public class AuthenticationListener implements ApplicationListener<ApplicationReadyEvent> {

	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final AncVisitRepository ancVisitRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("Authenticating to {}", qontakProperties.getWhatsApp().getBaseUrl());

		AuthRequest requestBody = createQontakAuthRequestBody();
		Mono<AuthResponse> response = webClient.post().uri(qontakProperties.getWhatsApp().getApiPathAuthentication())
			.bodyValue(requestBody).retrieve().bodyToMono(AuthResponse.class);

		AuthResponse responseBody = response.block();
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

	private AuthRequest createQontakAuthRequestBody() {
		AuthRequest requestBody = new AuthRequest();
		requestBody.setClient_id(qontakProperties.getWhatsApp().getClientId());
		requestBody.setClient_secret(qontakProperties.getWhatsApp().getClientSecret());
		requestBody.setGrant_type("password");
		requestBody.setUsername(qontakProperties.getWhatsApp().getUsername());
		requestBody.setPassword(qontakProperties.getWhatsApp().getPassword());
		return requestBody;
	}

	private void syncLastId() {
		Optional<Long> optNewPregnantWomenLastId = motherIdentityRepository
			.findFirstPregnantWomanByOrderByEventIdDesc();
		if (optNewPregnantWomenLastId.isPresent()) {
			lastIdProperties.getMotherIdentity().setPregnantMotherLastId(optNewPregnantWomenLastId.get());
		}
		Optional<Long> optEditedPregnantWomenLastId = motherEditRepository.findFirstPregnantWomanByOrderByEventIdDesc();
		if (optEditedPregnantWomenLastId.isPresent()) {
			lastIdProperties.getMotherEdit().setPregnantMotherLastId(optEditedPregnantWomenLastId.get());
		}
		Optional<Long> optNewNonPregnantWomenLastId = motherIdentityRepository
			.findFirstNonPregnantWomanByOrderByEventIdDesc();
		if (optNewNonPregnantWomenLastId.isPresent()) {
			lastIdProperties.getMotherIdentity().setNonPregnantMotherLastId(optNewNonPregnantWomenLastId.get());
		}
		Optional<Long> optEditedNonPregnantWomenLastId = motherEditRepository
			.findFirstNonPregnantWomanByOrderByEventIdDesc();
		if (optEditedNonPregnantWomenLastId.isPresent()) {
			lastIdProperties.getMotherEdit().setNonPregnantMotherLastId(optEditedNonPregnantWomenLastId.get());
		}

		Optional<Long> optAncVisitPregnancyGapLastId = ancVisitRepository.findLastEventId();
		if (optAncVisitPregnancyGapLastId.isPresent()) {
			lastIdProperties.setAncVisitPregnancyGapLastId(optAncVisitPregnancyGapLastId.get());
		}
		log.info("Sync-ed last ID from DB successfully.");
	}
}
