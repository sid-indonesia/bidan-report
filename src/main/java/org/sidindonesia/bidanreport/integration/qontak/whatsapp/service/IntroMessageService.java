package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class IntroMessageService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final QontakProperties qontakProperties;
	private final BroadcastMessageService broadcastMessageService;

	@Scheduled(fixedRateString = "${scheduling.intro-message.fixed-rate-in-ms}", initialDelayString = "${scheduling.intro-message.initial-delay-in-ms}")
	public void sendIntroMessageToNewMothersViaWhatsApp() {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\"...");

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
		newPregnantWomenIdentities.parallelStream().forEach(broadcastIntroMessageViaWhatsApp(
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
		editedPregnantWomenIds.parallelStream().forEach(broadcastIntroMessageViaWhatsApp(
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
			.forEach(broadcastIntroMessageViaWhatsApp(newEnrolledNonPregnantWomenSuccessCount,
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
		editedNonPregnantWomenIds.parallelStream().forEach(broadcastIntroMessageViaWhatsApp(
			editedNonPregnantWomenSuccessCount, qontakProperties.getWhatsApp().getNonPregnantWomanMessageTemplateId()));

		if (!editedNonPregnantWomenIds.isEmpty()) {
			lastIdProperties.getMotherEdit().setNonPregnantMotherLastId(
				editedNonPregnantWomenIds.get(editedNonPregnantWomenIds.size() - 1).getEventId());
			log.info("\"Send Join Notification via WhatsApp\" for edited non-pregnant women completed.");
			log.info("{} out of {} edited non-pregnant women have been notified via WhatsApp successfully.",
				editedNonPregnantWomenSuccessCount, editedNonPregnantWomenIds.size());
		}
	}

	private Consumer<MotherIdentityWhatsAppProjection> broadcastIntroMessageViaWhatsApp(AtomicLong successCount,
		String messageTemplateId) {
		return motherIdentity -> {
			BroadcastRequest requestBody = createIntroMessageRequestBody(motherIdentity, messageTemplateId);
			broadcastMessageService.sendBroadcastRequestToQontakAPI(successCount, motherIdentity, requestBody);
		};
	}

	private BroadcastRequest createIntroMessageRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(motherIdentity, messageTemplateId);

		setParametersForIntroMessage(motherIdentity, requestBody);
		return requestBody;
	}

	private void setParametersForIntroMessage(MotherIdentityWhatsAppProjection motherIdentity,
		BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "dho", qontakProperties.getWhatsApp().getDistrictHealthOfficeName());
		requestBody.setParameters(parameters);
	}
}