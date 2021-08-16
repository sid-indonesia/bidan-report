package org.sidindonesia.bidanreport.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.exception.ExcelWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

	@Autowired
	private ExcelSheetController excelSheetController;

	@BeforeEach
	public void setUp() {
	}

	@Test
	void testDownloadAllTablesAsExcelSheets_withHappyFlow() throws Exception {
		ResponseEntity<Resource> response = excelSheetController.downloadAllTablesAsExcelSheets();
		assertThat(response.getBody()).isInstanceOf(InputStreamResource.class);
	}
}

@IntegrationTest
@Transactional
@SpringBootTest(properties = "spring.jpa.properties.hibernate.hbm2ddl.auto=none")
class ExcelSheetControllerFailingTests {

	@Autowired
	private ExcelSheetController excelSheetController;

	@Test
	void testDownloadAllTablesAsExcelSheets_withNoDatabaseTablesGenerated_thenExcelWriteExceptionWillBeThrown() {
		assertThrows(ExcelWriteException.class, () -> {
			excelSheetController.downloadAllTablesAsExcelSheets();
		});
	}
}