package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.web.response.FileUploadResponse;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ParametersWithHeader;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.QRCodeService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.sidindonesia.bidanreport.service.LastIdService;
import org.sidindonesia.bidanreport.util.IndonesiaPhoneNumberUtil;
import org.sidindonesia.bidanreport.web.response.BundleTransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import ca.uhn.fhir.context.FhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PregnancyGapService {
	private static final String POSITIF = "positif";
	private static final String HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES = "https://sid-indonesia.org/clinical-codes";
	private static final String HTTP_LOINC_ORG = "http://loinc.org";
	public static final String QR_CODE_GAP_CARE_PNG = "QR_Code-gap_care-FHIR.png";
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final LastIdProperties lastIdProperties;
	private final LastIdService lastIdService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;
	private final QRCodeService qrCodeService;
	private final FhirContext fhirContext;
	@Autowired
	@Qualifier("webClientHapiFhirServer")
	private WebClient webClientHapiFhirServer;
	@Value("${hapi-fhir-server.base-url}")
	private String hapiFhirBaseUrl;

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

			pairs.parallelStream().forEach(pair -> broadcastMessageService
				.sendBroadcastDirectRequestToQontakAPI(pregnantGapSuccessCount, pair.getFirst(), pair.getSecond()));

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

		ParametersWithHeader parameters = new ParametersWithHeader();

		fillHeaderWithQRCodeImage(motherIdentity, values, parameters);

		parameters.addBodyWithValues("1", "full_name", motherIdentity.getFullName());
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

	private void fillHeaderWithQRCodeImage(PregnancyGapProjection motherIdentity, List<String> values,
		ParametersWithHeader parameters) {

		// FHIR Resource
		String prettyJson = postTransactionBundleToFHIRServerThenReturnURLPatientOperationEverything(motherIdentity,
			values);

		FileUploadResponse responseBody = qrCodeService.createQRCodeImageThenUploadToQontak(prettyJson);
		if (responseBody != null) {
			if ("success".equals(responseBody.getStatus())) {
				parameters.getHeader().addHeaderParam("url", responseBody.getData().getUrl());
				parameters.getHeader().addHeaderParam("filename", QR_CODE_GAP_CARE_PNG);
			} else {
				log.error(
					"Upload QR Code Gap Care PNG file failed for: {}, at phone number: {}, with error details: {}",
					motherIdentity.getFullName(), motherIdentity.getMobilePhoneNumber(), responseBody.getError());
			}
		} else {
			log.error("Upload QR Code Gap Care PNG file failed with no content for: {}, at phone number: {}",
				motherIdentity.getFullName(), motherIdentity.getMobilePhoneNumber());
		}
	}

	public String postTransactionBundleToFHIRServerThenReturnURLPatientOperationEverything(
		PregnancyGapProjection motherIdentity, List<String> values) {

		Patient patient = createPatientResource(motherIdentity);

		String patientUUID = "urn:uuid:" + UUID.randomUUID();
		Bundle bundle = new Bundle().setType(BundleType.TRANSACTION)
			.addEntry(new BundleEntryComponent().setResource(patient).setFullUrl(patientUUID)
				.setRequest(new BundleEntryRequestComponent().setMethod(HTTPVerb.POST)
					.setUrl(patient.getResourceType().toString())));
		Reference referencePatient = new Reference().setReference(patientUUID);
		Type ancEffectiveDateTime = new DateTimeType()
			.setValue(Date.valueOf(values.get(0).equalsIgnoreCase("N/A") ? LocalDate.EPOCH.toString() : values.get(0)));

		fillBundleWithObservationResources(values, bundle, referencePatient, ancEffectiveDateTime);

		String encodedJSON = fhirContext.newJsonParser().encodeResourceToString(bundle);
		Mono<BundleTransactionResponse> response = webClientHapiFhirServer.post().bodyValue(encodedJSON).retrieve()
			.bodyToMono(BundleTransactionResponse.class);

		BundleTransactionResponse transactionResponseBundle = response.block();
		if (transactionResponseBundle == null) {
			return null;
		} else {
			String location = transactionResponseBundle.getEntryFirstRep().getResponse().getLocation();
			return hapiFhirBaseUrl + "/" + location.substring(0, 1 + location.indexOf("/", 1 + location.indexOf("/")))
				+ "$everything";
		}
	}

	private Patient createPatientResource(PregnancyGapProjection motherIdentity) {
		Patient patient = new Patient();

		HumanName name = patient.addName();
		name.setText(motherIdentity.getFullName());

		if (motherIdentity.getFullName() != null) {
			String[] namesSplittedIntoTwo = motherIdentity.getFullName().split(" ", 2);
			if (namesSplittedIntoTwo.length > 1) {
				name.addGiven(namesSplittedIntoTwo[0]).setFamily(namesSplittedIntoTwo[1]);
			} else {
				name.addGiven(motherIdentity.getFullName());
			}
		}

		patient.addTelecom().setSystem(ContactPointSystem.PHONE)
			.setValue(IndonesiaPhoneNumberUtil.sanitize(motherIdentity.getMobilePhoneNumber()))
			.setUse(ContactPointUse.MOBILE).setRank(1);
		return patient;
	}

	private void fillBundleWithObservationResources(List<String> values, Bundle bundle, Reference referencePatient,
		Type ancEffectiveDateTime) {
		BundleEntryRequestComponent requestComponentPostObservation = new BundleEntryRequestComponent()
			.setMethod(HTTPVerb.POST).setUrl("Observation");

		if (!values.get(2).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(2))).setUnit("cm"),
				new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "8302-2", "Body height"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "body-height", "Body Height"))));
		}

		if (!values.get(3).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(3))).setUnit("kg"),
				new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "29463-7", "Body Weight"))
					.addCoding(new Coding(HTTP_LOINC_ORG, "3141-9", "Body weight Measured"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "body-weight", "Body Weight"))));
		}

		if (!values.get(4).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(4))).setUnit("cm"),
				new CodeableConcept()
					.addCoding(new Coding(HTTP_LOINC_ORG, "56072-2", "Circumference Mid upper arm - right"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "mid-upper-arm-circumference",
						"Mid upper arm circumference"))));
		}

		if (!values.get(5).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(5))).setUnit("mmHg"),
				new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "8480-6", "Systolic blood pressure"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "systolic-blood-pressure",
						"Systolic blood pressure"))));
		}

		if (!values.get(6).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(6))).setUnit("mmHg"),
				new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "8462-4", "Diastolic blood pressure"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "diastolic-blood-pressure",
						"Diastolic blood pressure"))));
		}

		if (!values.get(7).equalsIgnoreCase("-") && !values.get(7).equalsIgnoreCase("Janin belum teraba")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new Quantity(Double.valueOf(values.get(7).replace(" cm", ""))).setUnit("cm"),
					new CodeableConcept()
						.addCoding(new Coding(HTTP_LOINC_ORG, "11881-0", "Uterus Fundal height Tape measure"))
						.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "uterine-fundal-height",
							"Uterine Fundal height"))));
		}

		if (!values.get(8).equalsIgnoreCase("-") && !values.get(8).equalsIgnoreCase("Janin belum teraba")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient, new StringType(values.get(8)),
					new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "11877-8", "Fetal presentation US"))
						.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "fetal-presentation",
							"Fetal presentation"))));
		}

		if (!values.get(9).equalsIgnoreCase("-")
			&& !values.get(9).equalsIgnoreCase("Detak jantung janin belum terdengar")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Long.valueOf(values.get(9))).setUnit("/min"),
				new CodeableConcept().addCoding(new Coding(HTTP_LOINC_ORG, "55283-6", "Fetal Heart rate")).addCoding(
					new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "fetal-heart-rate", "Fetal Heart rate"))));
		}

		if (!values.get(10).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient, new StringType(values.get(10)),
					new CodeableConcept().addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES,
						"tetanus-toxoid-immunization-status", "Tetanus toxoid immunization status"))));
		}

		if (!values.get(11).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new BooleanType(values.get(11).equalsIgnoreCase("Ya")),
				new CodeableConcept().addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES,
					"is-given-tetanus-toxoid-injection", "Is given tetanus toxoid injection"))));
		}

		if (!values.get(12).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new BooleanType(values.get(12).equalsIgnoreCase("Ya")),
				new CodeableConcept().addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES,
					"is-given-iron-folic-acid-tablet", "Is given iron folic acid tablet"))));
		}

		if (!values.get(13).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new BooleanType(values.get(13).equalsIgnoreCase(POSITIF)), new CodeableConcept().addCoding(
						new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "has-proteinuria", "Has proteinuria"))));
		}

		if (!values.get(14).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new Quantity(Double.valueOf(values.get(14))).setUnit("g/dL"),
				new CodeableConcept()
					.addCoding(new Coding(HTTP_LOINC_ORG, "718-7", "Hemoglobin [Mass/volume] in Blood"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "hb-level-lab-test-result",
						"Hb level lab test result"))));
		}

		if (!values.get(15).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new BooleanType(values.get(15).equalsIgnoreCase("\u003e_140_mg_dl")),
					new CodeableConcept().addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES,
						"is-glucose-blood-more-than-140-mg-dl", "Is glucose blood more than 140 mg/dL"))));
		}

		if (!values.get(16).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new BooleanType(values.get(16).equalsIgnoreCase(POSITIF)), new CodeableConcept().addCoding(
						new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "has-thalasemia", "Has thalasemia"))));
		}

		if (!values.get(17).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new BooleanType(values.get(17).equalsIgnoreCase(POSITIF)), new CodeableConcept().addCoding(
						new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "has-syphilis", "Has syphilis"))));
		}

		if (!values.get(18).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation).setResource(createObservation(
				ancEffectiveDateTime, referencePatient, new BooleanType(values.get(18).equalsIgnoreCase(POSITIF)),
				new CodeableConcept()
					.addCoding(new Coding(HTTP_LOINC_ORG, "75410-1",
						"Hepatitis B virus surface Ag [Presence] in Serum, Plasma or Blood by Rapid immunoassay"))
					.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "has-hbsag", "Has HBsAg"))));
		}

		if (!values.get(19).equalsIgnoreCase("-")) {
			addEntryObservation(bundle, requestComponentPostObservation)
				.setResource(createObservation(ancEffectiveDateTime, referencePatient,
					new BooleanType(values.get(19).equalsIgnoreCase(POSITIF)), new CodeableConcept()
						.addCoding(new Coding(HTTPS_SID_INDONESIA_ORG_CLINICAL_CODES, "has-hiv", "Has HIV"))));
		}
	}

	private BundleEntryComponent addEntryObservation(Bundle bundle,
		BundleEntryRequestComponent requestComponentPostObservation) {
		return bundle.addEntry().setRequest(requestComponentPostObservation);
	}

	private Observation createObservation(Type effective, Reference referencePatient, Type value,
		CodeableConcept code) {
		Observation observation = new Observation();
		observation.setStatus(ObservationStatus.FINAL);
		observation.setCode(code);
		observation.setEffective(effective);
		observation.setSubject(referencePatient);
		observation.setValue(value);
		return observation;
	}
}