package org.sidindonesia.bidanreport.service;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.FhirResourceService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.VisitReminderService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.BroadcastMessageService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.QRCodeService;
import org.sidindonesia.bidanreport.repository.projection.AncVisitReminderProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
class FhirResourceServiceTest {

	@Autowired
	private QRCodeService qrCodeService;

	@Autowired
	private FhirResourceService fhirResourceService;

	@Value("${hapi-fhir-server.base-url}")
	private String hapiFhirBaseUrl;

	@Autowired
	private BroadcastMessageService broadcastMessageService;

	@Autowired
	private QontakProperties qontakProperties;

	@Autowired
	private VisitReminderService visitReminderService;

	@Test
	void testGenerateQRCode() throws Exception {
		// given
		AncVisitReminderProjection ancVisitReminderProjection = new AncVisitReminderProjection() {

			@Override
			public String getMobilePhoneNumber() {
				return "081234567890";
			}

			@Override
			public String getFullName() {
				return "Test Patient " + LocalDate.now();
			}

			@Override
			public long getEventId() {
				return 0;
			}

			@Override
			public String getPregnancyGapCommaSeparatedValues() {
				return LocalDate.now()
					+ ",28,154,48,23.5,100,60,20 cm,kepala,140,tt_ke_3,Ya,Ya,negatif,10.9,<_140_mg_dl,negatif,negatif,negatif,negatif";
			}

			@Override
			public Integer getLatestAncVisitNumber() {
				return 3;
			}
		};

		String csv = ancVisitReminderProjection.getPregnancyGapCommaSeparatedValues();
		List<String> values = Stream.of(csv.split(",")).map(String::trim).collect(toList());

		// when
		String urlFHIRPatientOperationEverything = fhirResourceService
			.postTransactionBundleToFHIRServerThenReturnURLPatientOperationEverything(ancVisitReminderProjection,
				values);

		qrCodeService.createQRCodeImage(urlFHIRPatientOperationEverything);

		// then
		assertThat(urlFHIRPatientOperationEverything).startsWith(hapiFhirBaseUrl);
		assertThat(qrCodeService).isNotNull();
		System.out.println(urlFHIRPatientOperationEverything);
	}

	@Disabled("test for using real Qontak API, not mock")
	@Test
	void testSendVisitReminderWithHeaderImage() throws Exception {
		AncVisitReminderProjection ancVisitReminderProjection = new AncVisitReminderProjection() {

			@Override
			public String getMobilePhoneNumber() {
				return "083812345678";
			}

			@Override
			public String getFullName() {
				return "Levi";
			}

			@Override
			public long getEventId() {
				return 0;
			}

			@Override
			public String getPregnancyGapCommaSeparatedValues() {
				return LocalDate.now()
					+ ",28,154,48,23.5,100,60,20 cm,kepala,140,tt_ke_3,Ya,Ya,negatif,10.9,<_140_mg_dl,negatif,negatif,negatif,negatif";
			}

			@Override
			public Integer getLatestAncVisitNumber() {
				return 2;
			}
		};

		broadcastMessageService.sendBroadcastDirectRequestToQontakAPI(new AtomicLong(), ancVisitReminderProjection,
			visitReminderService.createANCVisitReminderMessageRequestBody(ancVisitReminderProjection,
				qontakProperties.getWhatsApp().getVisitReminderWithHeaderImageMessageTemplateId(), true));

		assertThat(broadcastMessageService).isNotNull();
	}
}
