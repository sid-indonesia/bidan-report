package org.sidindonesia.bidanreport.service;

import org.junit.jupiter.api.BeforeEach;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.domain.BasicEntity;
import org.sidindonesia.bidanreport.repository.BasicEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link ExcelSheetService}.
 */
@IntegrationTest
@Transactional
class ExcelSheetServiceIT {

	private static final String DEFAULT_ID = "UUIDUUID-UUID-UUID-UUID-UUIDUUIDUUID";

	private static final String DEFAULT_ID_2 = "UUID2UUI-D2UU-ID2U-UID2-UUID2UUID2UU";

	private static final String DEFAULT_EMAIL = "johndoe@localhost";

	private static final String DEFAULT_EMAIL_2 = "johndoe2@localhost";

	private static final String DEFAULT_GIVEN_NAME = "john";

	private static final String DEFAULT_FAMILY_NAME = "doe";

	private static final String DEFAULT_CREATED_BY = "TEST";

	@Autowired
	private BasicEntityRepository basicEntityRepository;

	@Autowired
	private ExcelSheetService excelSheetService;

	private BasicEntity basicEntity;

	@BeforeEach
	public void setUp() {
		createBasicEntity(DEFAULT_ID, DEFAULT_EMAIL, DEFAULT_GIVEN_NAME, DEFAULT_FAMILY_NAME, DEFAULT_CREATED_BY);

		basicEntityRepository.save(basicEntity);
	}

	// TODO create @Test

	private void createBasicEntity(String id, String email, String givenName, String familyName, String createdBy) {
		basicEntity = new BasicEntity();
		basicEntity.setId(id);
		basicEntity.setEmail(email);
		basicEntity.setGivenName(givenName);
		basicEntity.setFamilyName(familyName);
		basicEntity.setCreatedBy(createdBy);
	}
}
