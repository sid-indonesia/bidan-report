package org.sidindonesia.bidanreport.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.exception.ExcelWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@SpringBootTest(properties = "spring.jpa.properties.hibernate.hbm2ddl.auto=drop")
@DirtiesContext
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