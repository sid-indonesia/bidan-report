package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.QRCodeService;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link PregnancyGapService}.
 */
@IntegrationTest
@Transactional
class PregnancyGapServiceTest {

	@Autowired
	private PregnancyGapService pregnancyGapService;
	@Autowired
	private DummyDataService dummyDataService;
	@Autowired
	private QRCodeService qrCodeService;
	@Value("${hapi-fhir-server.base-url}")
	private String hapiFhirBaseUrl;

	private void insertDummyDataForPregnancyGap() {
		dummyDataService.insertDummyData();
		IntStream.rangeClosed(1, 6).forEach(dummyDataService.insertIntoAncVisitForPregnancyGap());
	}

	@Test
	void testSendPregnancyGapMessageToEnrolledPregnantWomen_withHappyFlow() {
		// given
		insertDummyDataForPregnancyGap();
		// when
		pregnancyGapService.sendPregnancyGapMessageToEnrolledMothers();
		// then
		assertThat(pregnancyGapService).isNotNull();
	}

	@Test
	void testSendPregnancyGapMessageToEnrolledPregnantWomen_withNoDataExisted() {
		// when
		pregnancyGapService.sendPregnancyGapMessageToEnrolledMothers();
		// then
		assertThat(pregnancyGapService).isNotNull();
	}

	@Test
	void testPostTransactionBundleToFHIRServer() throws Exception {
		// given
		PregnancyGapProjection projection = new PregnancyGapProjection() {

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
		};

		String csv = projection.getPregnancyGapCommaSeparatedValues();
		List<String> values = Stream.of(csv.split(",")).map(String::trim).collect(toList());

		// when
		String urlFHIRPatientOperationEverything = pregnancyGapService
			.postTransactionBundleToFHIRServerThenReturnURLPatientOperationEverything(projection, values);

		qrCodeService.createQRCodeImage(urlFHIRPatientOperationEverything);

		// then
		assertThat(urlFHIRPatientOperationEverything).startsWith(hapiFhirBaseUrl);
	}
}
