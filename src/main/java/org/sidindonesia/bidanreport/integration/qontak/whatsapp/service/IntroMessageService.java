package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
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
@Transactional
@Service
public class IntroMessageService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final QontakProperties qontakProperties;
	private final BroadcastMessageService broadcastMessageService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;

	@Scheduled(fixedRateString = "${scheduling.intro-message.fixed-rate-in-ms}", initialDelayString = "${scheduling.intro-message.initial-delay-in-ms}")
	public void sendIntroMessageToNewMothersViaWhatsApp() {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\"...");

		processNewPregnantWomen();
		processEditedPregnantWomen();
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

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId(),
				"intro_pregnant_woman", newEnrolledPregnantWomenSuccessCount.get(),
				newPregnantWomenIdentities.size() - newEnrolledPregnantWomenSuccessCount.get());
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

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnantWomanMessageTemplateId(),
				"intro_pregnant_woman", editedPregnantWomenSuccessCount.get(),
				editedPregnantWomenIds.size() - editedPregnantWomenSuccessCount.get());
		}
	}

	private Consumer<MotherIdentityWhatsAppProjection> broadcastIntroMessageViaWhatsApp(AtomicLong successCount,
		String messageTemplateId) {
		return motherIdentity -> {
			BroadcastDirectRequest requestBody = createIntroMessageRequestBody(motherIdentity, messageTemplateId);
			broadcastMessageService.sendBroadcastDirectRequestToQontakAPI(successCount, motherIdentity, requestBody);
		};
	}

	private BroadcastDirectRequest createIntroMessageRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastDirectRequest requestBody = broadcastMessageService.createBroadcastDirectRequestBody(motherIdentity,
			messageTemplateId);

		setParametersForIntroMessage(motherIdentity, requestBody);
		return requestBody;
	}

	private void setParametersForIntroMessage(MotherIdentityWhatsAppProjection motherIdentity,
		BroadcastDirectRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "dho", qontakProperties.getWhatsApp().getDistrictHealthOfficeName());
		requestBody.setParameters(parameters);
	}
}