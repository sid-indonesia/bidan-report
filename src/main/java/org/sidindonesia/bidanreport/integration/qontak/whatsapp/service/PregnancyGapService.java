package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
public class PregnancyGapService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;

	@Scheduled(cron = "${scheduling.pregnancy-gap.cron}", zone = "${scheduling.pregnancy-gap.zone}")
	public void sendPregnancyGapMessageToEnrolledMothers() {
		log.debug("Executing scheduled \"Inform Pregnancy Gap via WhatsApp\"...");
		log.debug("Send pregnancy gap message to all mothers with +"
			+ qontakProperties.getWhatsApp().getPregnancyGapIntervalInDays() + " day(s) latest ANC visit date");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherIdentityRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(
				qontakProperties.getWhatsApp().getPregnancyGapIntervalInDays());

		broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy);
	}

	private void processRowsFromMotherEdit() {
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherEditRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(
				qontakProperties.getWhatsApp().getPregnancyGapIntervalInDays());

		broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy);
	}

	private void broadcastPregnancyGapMessageTo(
		List<MotherIdentityWhatsAppProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy) {
		if (!allPregnantWomenToBeInformedOfGapInTheirPregnancy.isEmpty()) {
			AtomicLong pregnantGapSuccessCount = new AtomicLong();
			allPregnantWomenToBeInformedOfGapInTheirPregnancy.parallelStream()
				.forEach(broadcastPregnancyGapMessageViaWhatsApp(pregnantGapSuccessCount,
					qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId()));
			log.info("\"Inform Pregnancy Gap via WhatsApp\" for enrolled pregnant women completed.");
			log.info(
				"{} out of {} enrolled pregnant women have been informed of the gap in their pregnancy via WhatsApp successfully.",
				pregnantGapSuccessCount, allPregnantWomenToBeInformedOfGapInTheirPregnancy.size());
		}
	}

	private Consumer<MotherIdentityWhatsAppProjection> broadcastPregnancyGapMessageViaWhatsApp(AtomicLong successCount,
		String messageTemplateId) {
		return motherIdentity -> {
			BroadcastRequest requestBody = createPregnancyGapMessageRequestBody(motherIdentity, messageTemplateId);
			broadcastMessageService.sendBroadcastRequestToQontakAPI(successCount, motherIdentity, requestBody);
		};
	}

	private BroadcastRequest createPregnancyGapMessageRequestBody(MotherIdentityWhatsAppProjection motherIdentity,
		String messageTemplateId) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(motherIdentity,
			messageTemplateId);

		setParametersForPregnancyGapMessage(motherIdentity, requestBody);
		return requestBody;
	}

	private void setParametersForPregnancyGapMessage(MotherIdentityWhatsAppProjection motherIdentity,
		BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "date",
			LocalDate.now().minusDays(qontakProperties.getWhatsApp().getPregnancyGapIntervalInDays()).toString());
		parameters.addBodyWithValues("3", "gap1", "First Gap");
		parameters.addBodyWithValues("4", "gap2", "Second Gap");
		requestBody.setParameters(parameters);
	}
}