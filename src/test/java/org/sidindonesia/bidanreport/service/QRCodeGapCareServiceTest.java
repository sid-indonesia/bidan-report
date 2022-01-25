package org.sidindonesia.bidanreport.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.PregnancyGapService;
import org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util.QRCodeService;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@IntegrationTest
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/opensrp_test_lotim",
		"spring.jpa.properties.hibernate.default_schema=sid_bidan",
		"spring.jpa.properties.hibernate.hbm2ddl.auto=none" })
class QRCodeGapCareServiceTest {

	@Autowired
	private QRCodeService qrCodeService;

	@Autowired
	private MotherIdentityRepository motherIdentityRepository;

	@Autowired
	private PregnancyGapService pregnancyGapService;

	@Test
	void testGenerateQRCode() throws Exception {
		List<PregnancyGapProjection> allPregnantWomenToBeInformedOfGapInTheirPregnancy = motherIdentityRepository
			.findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(30000L);

		PregnancyGapProjection lastElement = allPregnantWomenToBeInformedOfGapInTheirPregnancy
			.get(allPregnantWomenToBeInformedOfGapInTheirPregnancy.size() - 1);

		String csv = lastElement.getPregnancyGapCommaSeparatedValues();
		List<String> values = Stream.of(csv.split(",")).map(String::trim).collect(toList());

		String jsonOfGapCareObject = pregnancyGapService.createJsonStringOfFHIRResource(lastElement, values);

		qrCodeService.createQRCodeImage(jsonOfGapCareObject);
	}
}
