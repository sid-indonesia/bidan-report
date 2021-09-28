package org.sidindonesia.bidanreport.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.jpatoexcel.exception.ExcelWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@SpringBootTest
@DirtiesContext
class ExcelSheetControllerFailingTests {

	@Autowired
	private ExcelSheetController excelSheetController;

	@Autowired
	JdbcOperations jdbcOperations;

	@BeforeEach
	void setUp() {
		jdbcOperations.execute("DROP TABLE anc_close");
	}

	@Test
	void testDownloadAllTablesAsExcelSheets_withOneDatabaseTableDropped_thenExcelWriteExceptionWillBeThrown() {
		assertThrows(ExcelWriteException.class, () -> {
			excelSheetController.downloadAllTablesAsExcelSheets();
		});
	}
}