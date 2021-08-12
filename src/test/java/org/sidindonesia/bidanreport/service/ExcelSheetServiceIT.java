package org.sidindonesia.bidanreport.service;

import org.junit.jupiter.api.BeforeEach;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.domain.AncClose;
import org.sidindonesia.bidanreport.repository.AncCloseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link ExcelSheetService}.
 */
@IntegrationTest
@Transactional
class ExcelSheetServiceIT {

	private static final long DEFAULT_ID = 1L;

	private static final String DEFAULT_ID_2 = "UUID2UUI-D2UU-ID2U-UID2-UUID2UUID2UU";

	@Autowired
	private AncCloseRepository ancCloseRepository;

	@Autowired
	private ExcelSheetService excelSheetService;

	private AncClose ancClose;

	@BeforeEach
	public void setUp() {
		createAncClose(DEFAULT_ID);

		ancCloseRepository.save(ancClose);
	}

	// TODO create @Test

	private void createAncClose(long id) {
		ancClose = new AncClose();
		ancClose.setEventId(id);
	}
}
