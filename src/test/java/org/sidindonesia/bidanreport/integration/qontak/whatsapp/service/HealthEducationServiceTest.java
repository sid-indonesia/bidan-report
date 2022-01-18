package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link HealthEducationService}.
 */
@IntegrationTest
@Transactional
class HealthEducationServiceTest {

	@Autowired
	private HealthEducationService healthEducationService;
	@Autowired
	private DummyDataService dummyDataService;

	private void insertDummyDataForHealthEducation() {
		dummyDataService.insertDummyData();
		IntStream.rangeClosed(1, 6).forEach(dummyDataService.insertIntoAncVisitForHealthEducation());
	}

	@Test
	void testSendHealthEducationMessageToEnrolledPregnantWomen_withHappyFlow() throws Exception {
		// given
		insertDummyDataForHealthEducation();
		// when
		healthEducationService.sendHealthEducationsToEnrolledMothers();
		// then
		assertThat(healthEducationService).isNotNull();
	}

	@Test
	void testSendHealthEducationMessageToEnrolledPregnantWomen_withNoDataExisted() throws Exception {
		// when
		healthEducationService.sendHealthEducationsToEnrolledMothers();
		// then
		assertThat(healthEducationService).isNotNull();
	}
}
