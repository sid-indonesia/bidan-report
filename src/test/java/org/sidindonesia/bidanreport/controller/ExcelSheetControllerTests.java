package org.sidindonesia.bidanreport.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.repository.MotherIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link ExcelSheetController}.
 */
@IntegrationTest
@Transactional
class ExcelSheetControllerTests {

	private static final Long DEFAULT_ID = 1L;

	@Autowired
	private ExcelSheetController excelSheetController;

	@Autowired
	private MotherIdentityRepository motherIdentityRepository;

	private MotherIdentity motherIdentity;

	@BeforeEach
	public void setUp() {
		createMotherIdentity();
		motherIdentityRepository.save(motherIdentity);
	}

	@AfterEach
	public void tearDown() {
		motherIdentityRepository.delete(motherIdentity);
	}

	@Test
	void testDownloadAllTablesAsExcelSheets_withHappyFlow() throws Exception {
		ResponseEntity<Resource> response = excelSheetController.downloadAllTablesAsExcelSheets();
		assertThat(response.getBody()).isInstanceOf(InputStreamResource.class);
	}

	private MotherIdentity createMotherIdentity() {
		motherIdentity = new MotherIdentity();
		motherIdentity.setEventId(DEFAULT_ID);
		return motherIdentity;

	}
}