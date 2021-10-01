package org.sidindonesia.bidanreport.integration.qontak.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppBroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.request.QontakWhatsAppBroadcastRequest.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.response.QontakWhatsAppBroadcastResponse;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.sidindonesia.bidanreport.util.IndonesiaPhoneNumberUtil;
import org.springframework.scheduling.annotation.Scheduled;
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
public class WhatsAppMessageService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;

	@Scheduled(fixedRateString = "${scheduling.fixed-rate-in-ms}", initialDelayString = "${scheduling.initial-delay-in-ms}")
	public void sendWhatsAppMessageToNewMothers() {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\"...");

		log.debug("Retrieving all new mother identities...");
		processNewPregnantWomen();
		processEditedPregnantWomen();
		processNewNonPregnantWomen();
		processEditedNonPregnantWomen();
	}

	private void processNewPregnantWomen() {
		List<MotherIdentityWhatsAppProjection> newPregnantWomenIdentities = motherIdentityRepository
			.findAllPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherIdentity().getPregnantMotherLastId());

		AtomicLong newEnrolledPregnantWomenSuccessCount = new AtomicLong();
		newPregnantWomenIdentities.parallelStream().forEach(broadcastDirectMessageViaWhatsApp(
			newEnrolledPregnantWomenSuccessCount, qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId()));

		if (!newPregnantWomenIdentities.isEmpty()) {
			lastIdProperties.getMotherIdentity().setPregnantMotherLastId(
				newPregnantWomenIdentities.get(newPregnantWomenIdentities.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" for new enrolled pregnant women completed.");
			log.info("{} out of {} new enrolled pregnant women have been notified via WhatsApp successfully.",
				newEnrolledPregnantWomenSuccessCount, newPregnantWomenIdentities.size());
		}
	}

	private void processEditedPregnantWomen() {
		List<MotherIdentityWhatsAppProjection> editedPregnantWomenIds = motherEditRepository
			.findAllPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherEdit().getPregnantMotherLastId());

		AtomicLong editedPregnantWomenSuccessCount = new AtomicLong();
		editedPregnantWomenIds.parallelStream().forEach(broadcastDirectMessageViaWhatsApp(
			editedPregnantWomenSuccessCount, qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId()));

		if (!editedPregnantWomenIds.isEmpty()) {
			lastIdProperties.getMotherEdit()
				.setPregnantMotherLastId(editedPregnantWomenIds.get(editedPregnantWomenIds.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" for edited pregnant women completed.");
			log.info("{} out of {} edited pregnant women have been notified via WhatsApp successfully.",
				editedPregnantWomenSuccessCount, editedPregnantWomenIds.size());
		}
	}

	private void processNewNonPregnantWomen() {
		List<MotherIdentityWhatsAppProjection> newNonPregnantWomenIdentities = motherIdentityRepository
			.findAllNonPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherIdentity().getNonPregnantMotherLastId());

		AtomicLong newEnrolledNonPregnantWomenSuccessCount = new AtomicLong();
		newNonPregnantWomenIdentities.parallelStream()
			.forEach(broadcastDirectMessageViaWhatsApp(newEnrolledNonPregnantWomenSuccessCount,
				qontakProperties.getWhatsApp().getNonPregnantWomanMessageTemplateId()));

		if (!newNonPregnantWomenIdentities.isEmpty()) {
			lastIdProperties.getMotherIdentity().setNonPregnantMotherLastId(
				newNonPregnantWomenIdentities.get(newNonPregnantWomenIdentities.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" for new enrolled non-pregnant women completed.");
			log.info("{} out of {} new enrolled non-pregnant women have been notified via WhatsApp successfully.",
				newEnrolledNonPregnantWomenSuccessCount, newNonPregnantWomenIdentities.size());
		}
	}

	private void processEditedNonPregnantWomen() {
		List<MotherIdentityWhatsAppProjection> editedNonPregnantWomenIds = motherEditRepository
			.findAllNonPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
				lastIdProperties.getMotherEdit().getNonPregnantMotherLastId());

		AtomicLong editedNonPregnantWomenSuccessCount = new AtomicLong();
		editedNonPregnantWomenIds.parallelStream().forEach(broadcastDirectMessageViaWhatsApp(
			editedNonPregnantWomenSuccessCount, qontakProperties.getWhatsApp().getNonPregnantWomanMessageTemplateId()));

		if (!editedNonPregnantWomenIds.isEmpty()) {
			lastIdProperties.getMotherEdit().setNonPregnantMotherLastId(
				editedNonPregnantWomenIds.get(editedNonPregnantWomenIds.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" for edited non-pregnant women completed.");
			log.info("{} out of {} edited non-pregnant women have been notified via WhatsApp successfully.",
				editedNonPregnantWomenSuccessCount, editedNonPregnantWomenIds.size());
		}
	}

	private Consumer<? super MotherIdentityWhatsAppProjection> broadcastDirectMessageViaWhatsApp(
		AtomicLong successCount, String messageTemplateId) {
		return motherIdentity -> {
			QontakWhatsAppBroadcastRequest requestBody = createBroadcastDirectRequestBody(motherIdentity,
				messageTemplateId);
			Mono<QontakWhatsAppBroadcastResponse> response = webClient.post()
				.uri(qontakProperties.getWhatsApp().getApiPathBroadcastDirect()).bodyValue(requestBody)
				.header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
				.bodyToMono(QontakWhatsAppBroadcastResponse.class).onErrorResume(WebClientResponseException.class,
					ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
						? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), QontakWhatsAppBroadcastResponse.class))
						: Mono.error(ex));

			QontakWhatsAppBroadcastResponse responseBody = response.block();
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

	private QontakWhatsAppBroadcastRequest createBroadcastDirectRequestBody(
		MotherIdentityWhatsAppProjection motherIdentity, String messageTemplateId) {
		QontakWhatsAppBroadcastRequest requestBody = new QontakWhatsAppBroadcastRequest();
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