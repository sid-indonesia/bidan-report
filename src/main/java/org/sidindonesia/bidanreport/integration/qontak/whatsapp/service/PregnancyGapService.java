package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.sidindonesia.bidanreport.service.LastIdService;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PregnancyGapService {
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final LastIdProperties lastIdProperties;
	private final LastIdService lastIdService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;

	@Scheduled(fixedRateString = "${scheduling.pregnancy-gap.fixed-rate-in-ms}", initialDelayString = "${scheduling.pregnancy-gap.initial-delay-in-ms}")
	public void sendPregnancyGapMessageToEnrolledMothers() {
		log.debug("Executing scheduled \"Inform Pregnancy Gap via WhatsApp\"...");
		log.debug("Send pregnancy gap message to all mothers according to their latest ANC visit");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();

		lastIdService.syncANCVisitPregnancyGapLastId();
	}

	private void processRowsFromMotherIdentity() {
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherIdentityRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(lastIdProperties.getAncVisitPregnancyGapLastId());

		broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy);
	}

	private void processRowsFromMotherEdit() {
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherEditRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(lastIdProperties.getAncVisitPregnancyGapLastId());

		broadcastPregnancyGapMessageTo(allPregnantWomenToBeInformedOfGapInTheirPregnancy);
	}

	private void broadcastPregnancyGapMessageTo(
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy) {
		if (!allPregnantWomenToBeInformedOfGapInTheirPregnancy.isEmpty()) {
			AtomicLong pregnantGapSuccessCount = new AtomicLong();
			List<Pair<PregnancyGapProjection, BroadcastDirectRequest>> pairs = allPregnantWomenToBeInformedOfGapInTheirPregnancy
				.parallelStream().filter(motherIdentity -> motherIdentity.getPregnancyGapCommaSeparatedValues() != null)
				.map(motherIdentity -> createPregnancyGapMessageRequestBody(motherIdentity,
					qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId()))
				.collect(toList());

			pairs.parallelStream().forEach(pair -> {
				try {
					broadcastMessageService.sendBroadcastDirectRequestToQontakAPI(pregnantGapSuccessCount,
						pair.getFirst(), pair.getSecond());
				} catch (InterruptedException e) {
					log.warn("Broadcast Direct got interrupted! {}", e);
					Thread.currentThread().interrupt();
				}
			});

			log.info("\"Inform Pregnancy Gap via WhatsApp\" for enrolled pregnant women completed.");
			log.info(
				"{} out of {} enrolled pregnant women have been informed of the gap in their pregnancy via WhatsApp successfully.",
				pregnantGapSuccessCount, allPregnantWomenToBeInformedOfGapInTheirPregnancy.size());

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getPregnancyGapMessageTemplateId(),
				"pregnancy_gap", pregnantGapSuccessCount.get(),
				allPregnantWomenToBeInformedOfGapInTheirPregnancy.size() - pregnantGapSuccessCount.get());
		}
	}

	private Pair<PregnancyGapProjection, BroadcastDirectRequest> createPregnancyGapMessageRequestBody(
		PregnancyGapProjection motherIdentity, String messageTemplateId) {
		BroadcastDirectRequest requestBody = broadcastMessageService.createBroadcastDirectRequestBody(motherIdentity,
			messageTemplateId);

		setParametersForPregnancyGapMessage(motherIdentity, requestBody);
		return Pair.of(motherIdentity, requestBody);
	}

	private void setParametersForPregnancyGapMessage(PregnancyGapProjection motherIdentity,
		BroadcastDirectRequest requestBody) {
		String csv = motherIdentity.getPregnancyGapCommaSeparatedValues();
		List<String> values = Stream.of(csv.split(",")).map(String::trim).collect(toList());

		Parameters parameters = new Parameters();

		parameters.addBodyWithValues("1", "full_name",
			motherIdentity.getFullName() == null ? "-" : motherIdentity.getFullName());
		parameters.addBodyWithValues("2", "anc_date", values.get(0));
		parameters.addBodyWithValues("3", "gestational_age", values.get(1));
		parameters.addBodyWithValues("4", "height_in_cm", values.get(2));
		parameters.addBodyWithValues("5", "weight_in_kg", values.get(3));
		parameters.addBodyWithValues("6", "muac_in_cm", values.get(4));
		parameters.addBodyWithValues("7", "systolic_bp", values.get(5));
		parameters.addBodyWithValues("8", "diastolic_bp", values.get(6));
		parameters.addBodyWithValues("9", "uterine_f_height", values.get(7));
		parameters.addBodyWithValues("10", "fetal_presentati", values.get(8));
		parameters.addBodyWithValues("11", "fetal_heart_rate", values.get(9));
		parameters.addBodyWithValues("12", "tetanus_t_imm_st", values.get(10));
		parameters.addBodyWithValues("13", "given_tt_injecti", values.get(11));
		parameters.addBodyWithValues("14", "given_ifa_tablet", values.get(12));
		parameters.addBodyWithValues("15", "has_proteinuria", values.get(13));
		parameters.addBodyWithValues("16", "hb_level_result", values.get(14));
		parameters.addBodyWithValues("17", "glucose_140_mgdl", values.get(15));
		parameters.addBodyWithValues("18", "has_thalasemia", values.get(16));
		parameters.addBodyWithValues("19", "has_syphilis", values.get(17));
		parameters.addBodyWithValues("20", "has_hbsag", values.get(18));
		parameters.addBodyWithValues("21", "has_hiv", values.get(19));
		requestBody.setParameters(parameters);
	}
}