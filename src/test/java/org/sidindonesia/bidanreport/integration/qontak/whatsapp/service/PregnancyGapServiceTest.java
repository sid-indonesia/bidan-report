package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
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
}
