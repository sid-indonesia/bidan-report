package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link VisitReminderService}.
 */
@IntegrationTest
@Transactional
class VisitReminderServiceTest {

	@Autowired
	private VisitReminderService visitReminderService;
	@Autowired
	private DummyDataService dummyDataService;

	private void insertDummyDataForANCVisitReminder() {
		dummyDataService.insertDummyData();
		IntStream.rangeClosed(1, 6).forEach(dummyDataService.insertIntoAncVisitForVisitReminder());
	}

	@Test
	void testSendVisitReminderMessageToEnrolledPregnantWomen_withHappyFlow() {
		// given
		insertDummyDataForANCVisitReminder();
		// when
		visitReminderService.sendVisitRemindersToEnrolledMothers();
		// then
		assertThat(visitReminderService).isNotNull();
	}

	@Test
	void testSendVisitReminderMessageToEnrolledPregnantWomen_withNoDataExisted() {
		// when
		visitReminderService.sendVisitRemindersToEnrolledMothers();
		// then
		assertThat(visitReminderService).isNotNull();
	}
}
