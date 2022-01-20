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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class IntroNonPreggoService {
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final LastIdProperties lastIdProperties;
	private final QontakProperties qontakProperties;
	private final BroadcastMessageService broadcastMessageService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;

	public void sendIntroMessageToNewNonPregnantWomenViaWhatsApp() {
		log.debug("Executing scheduled \"Send Join Notification via WhatsApp\" for non-preggo women...");

		processNewNonPregnantWomen();
		processEditedNonPregnantWomen();
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

			automatedMessageStatsRepository.upsert(
				qontakProperties.getWhatsApp().getNonPregnantWomanMessageTemplateId(), "intro_non_pregnant_woman",
				newEnrolledNonPregnantWomenSuccessCount.get(),
				newNonPregnantWomenIdentities.size() - newEnrolledNonPregnantWomenSuccessCount.get());
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

			automatedMessageStatsRepository.upsert(
				qontakProperties.getWhatsApp().getNonPregnantWomanMessageTemplateId(), "intro_non_pregnant_woman",
				editedNonPregnantWomenSuccessCount.get(),
				editedNonPregnantWomenIds.size() - editedNonPregnantWomenSuccessCount.get());
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