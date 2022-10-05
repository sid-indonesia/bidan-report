package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;
import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListUtil.createContactListRequest;
import static org.sidindonesia.bidanreport.util.CSVUtil.FULL_NAME;
import static org.sidindonesia.bidanreport.util.CSVUtil.VISIT_NUMBER;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.repository.AutomatedMessageStatsRepository;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastDirectRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.BroadcastRequest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.Parameters;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ParametersWithHeader;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.ContactListService;
import org.sidindonesia.bidanreport.repository.MotherEditRepository;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.AncVisitReminderProjection;
import org.sidindonesia.bidanreport.util.CSVUtil;
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
public class VisitReminderService {
	private static final String MESSAGE_TEMPLATE_NAME = "anc_visit_reminder";
	private final QontakProperties qontakProperties;
	private final MotherIdentityRepository motherIdentityRepository;
	private final MotherEditRepository motherEditRepository;
	private final BroadcastMessageService broadcastMessageService;
	private final AutomatedMessageStatsRepository automatedMessageStatsRepository;
	private final FhirResourceService fhirResourceService;
	private final ContactListService contactListService;

	@Scheduled(cron = "${scheduling.visit-reminder.cron}", zone = "${scheduling.visit-reminder.zone}")
	public void sendVisitRemindersToEnrolledMothers() throws IOException, InterruptedException {
		log.debug("Executing scheduled \"Send ANC Visit Reminder via WhatsApp\"...");
		log.debug("Send ANC visit reminder to all mothers with -"
			+ qontakProperties.getWhatsApp().getVisitReminderIntervalInDays() + " day(s) for the next ANC visit date");
		processRowsFromMotherIdentity();
		processRowsFromMotherEdit();
	}

	private void processRowsFromMotherIdentity() throws IOException, InterruptedException {
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherIdentityRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays())
			.parallelStream()
			.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null)
			.collect(toList());

		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit, "mother_identity");
		}
	}

	private void processRowsFromMotherEdit() throws IOException, InterruptedException {
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit = motherEditRepository
			.findAllPregnantWomenToBeRemindedForTheNextANCVisit(qontakProperties.getWhatsApp().getVisitIntervalInDays(),
				qontakProperties.getWhatsApp().getVisitReminderIntervalInDays())
			.parallelStream()
			.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null)
			.collect(toList());

		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			broadcastANCVisitReminderMessageTo(allPregnantWomenToBeRemindedForTheNextANCVisit, "mother_edit");
		}
	}

	private void broadcastANCVisitReminderMessageTo(
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit, String fromTable)
		throws IOException, InterruptedException {

		// Create contact list CSV
		String contactsCsvFileName = qontakProperties.getWhatsApp().getAncVisitReminderContactListCsvAbsoluteFileName()
			.replace(".csv", "_" + fromTable + ".csv");
		CSVUtil.createContactListCSVFileForANCVisitReminderMessage(allPregnantWomenToBeRemindedForTheNextANCVisit,
			contactsCsvFileName);

		String campaignName = ZonedDateTime.now() + " " + qontakProperties.getWhatsApp().getDistrictHealthOfficeName()
			+ ", " + fromTable + " (ANC Visit Reminder)";
		// Hit API post contact list, get contact_list_id
		String contactListId = contactListService
			.sendCreateContactListRequestToQontakAPI(createContactListRequest(campaignName, contactsCsvFileName));

		if (contactListId != null) {

			if (contactListService.tryRetrieveContactListByIdMultipleTimes(contactListId)) {
				broadcastBulk(fromTable, allPregnantWomenToBeRemindedForTheNextANCVisit, campaignName, contactListId);
			}

		} else {
			log.error(
				"\"Send ANC Visit Reminder via WhatsApp\" for enrolled pregnant women failed due to error when POST contact list. ("
					+ fromTable + ")");
		}
	}

	private void broadcastBulk(String fromTable,
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit, String campaignName,
		String contactListId) throws InterruptedException {
		// Broadcast to contact_list
		boolean isSuccess = broadcastBulkMessageViaWhatsApp(
			qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(), contactListId, campaignName);

		log.info("\"Send ANC Visit Reminder via WhatsApp\" for enrolled pregnant women completed. (" + fromTable + ")");

		if (isSuccess) {
			log.info(
				"{} enrolled pregnant women have been reminded of the next ANC visit via WhatsApp successfully as bulk broadcast request.",
				allPregnantWomenToBeRemindedForTheNextANCVisit.size());
		}
	}

	private boolean broadcastBulkMessageViaWhatsApp(String messageTemplateId, String contactListId, String campaignName)
		throws InterruptedException {
		BroadcastRequest requestBody = createANCVisitReminderMessageRequestBody(messageTemplateId, contactListId,
			campaignName);
		return broadcastMessageService.sendBroadcastRequestToQontakAPI(requestBody);
	}

	private BroadcastRequest createANCVisitReminderMessageRequestBody(String messageTemplateId, String contactListId,
		String campaignName) {
		BroadcastRequest requestBody = broadcastMessageService.createBroadcastRequestBody(campaignName,
			messageTemplateId, contactListId);

		setParametersForANCVisitReminderMessage(requestBody);
		return requestBody;
	}

	private void setParametersForANCVisitReminderMessage(BroadcastRequest requestBody) {
		Parameters parameters = new Parameters();
		parameters.addBodyWithValues("1", FULL_NAME);
		parameters.addBodyWithValues("2", VISIT_NUMBER);
		requestBody.setParameters(parameters);
	}

	@SuppressWarnings("unused") // not deleted because the FHIR QR code still not refactored into broadcast bulk
	private void broadcastANCVisitReminderMessageTo(
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit) {
		if (!allPregnantWomenToBeRemindedForTheNextANCVisit.isEmpty()) {
			AtomicLong visitReminderSuccessCount = new AtomicLong();
			List<Pair<AncVisitReminderProjection, BroadcastDirectRequest>> pairs = allPregnantWomenToBeRemindedForTheNextANCVisit
				.parallelStream()
				.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null
					&& ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues() == null)
				.map(
					ancVisitReminderProjection -> Pair.of(ancVisitReminderProjection,
						createANCVisitReminderMessageRequestBody(ancVisitReminderProjection,
							qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(), false)))
				.collect(toList());

			pairs.parallelStream().forEach(pair -> broadcastMessageService
				.sendBroadcastDirectRequestToQontakAPI(visitReminderSuccessCount, pair.getFirst(), pair.getSecond()));

			// cannot do parallel because QR code file generation
			allPregnantWomenToBeRemindedForTheNextANCVisit.stream()
				.filter(ancVisitReminderProjection -> ancVisitReminderProjection.getLatestAncVisitNumber() != null
					&& ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues() != null)
				.forEach(ancVisitReminderProjection -> broadcastMessageService.sendBroadcastDirectRequestToQontakAPI(
					visitReminderSuccessCount, ancVisitReminderProjection,
					createANCVisitReminderMessageRequestBody(ancVisitReminderProjection,
						qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(), true)));

			log.info("\"Send ANC Visit Reminder via WhatsApp\" for enrolled pregnant women completed.");
			log.info(
				"{} out of {} enrolled pregnant women have been reminded of the next ANC visit via WhatsApp successfully.",
				visitReminderSuccessCount, allPregnantWomenToBeRemindedForTheNextANCVisit.size());

			automatedMessageStatsRepository.upsert(qontakProperties.getWhatsApp().getVisitReminderMessageTemplateId(),
				MESSAGE_TEMPLATE_NAME, visitReminderSuccessCount.get(),
				allPregnantWomenToBeRemindedForTheNextANCVisit.size() - visitReminderSuccessCount.get());
		}
	}

	public BroadcastDirectRequest createANCVisitReminderMessageRequestBody(
		AncVisitReminderProjection ancVisitReminderProjection, String messageTemplateId, boolean useQRCodeAsHeader) {
		BroadcastDirectRequest requestBody = broadcastMessageService
			.createBroadcastDirectRequestBody(ancVisitReminderProjection, messageTemplateId);

		setParametersForANCVisitReminderMessage(ancVisitReminderProjection, requestBody, useQRCodeAsHeader);
		return requestBody;
	}

	private void setParametersForANCVisitReminderMessage(AncVisitReminderProjection ancVisitReminderProjection,
		BroadcastDirectRequest requestBody, boolean useQRCodeAsHeader) {
		Parameters parameters;
		if (useQRCodeAsHeader) {
			parameters = new ParametersWithHeader();
			fhirResourceService.fillHeaderWithQRCodeImage(ancVisitReminderProjection,
				Stream.of(ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues().split(",")).map(String::trim)
					.collect(toList()),
				(ParametersWithHeader) parameters);
			requestBody.setMessage_template_id(
				qontakProperties.getWhatsApp().getVisitReminderWithHeaderImageMessageTemplateId());
		} else {
			parameters = new Parameters();
		}
		fillParameters(ancVisitReminderProjection, requestBody, parameters);
	}

	private void fillParameters(AncVisitReminderProjection ancVisitReminderProjection,
		BroadcastDirectRequest requestBody, Parameters parameters) {
		parameters.addBodyWithValues("1", "full_name",
			ancVisitReminderProjection.getFullName() == null ? "-" : ancVisitReminderProjection.getFullName());
		parameters.addBodyWithValues("2", "visit_number",
			String.valueOf(ancVisitReminderProjection.getLatestAncVisitNumber() + 1));
		requestBody.setParameters(parameters);
	}
}