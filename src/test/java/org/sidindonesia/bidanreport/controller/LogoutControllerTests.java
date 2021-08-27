package org.sidindonesia.bidanreport.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link ExcelSheetController}.
 */
@IntegrationTest
@Transactional
class LogoutControllerTests {

	@Autowired
	private LogoutController logoutController;

	@Test
	void testLogout() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		BasicUserPrincipal principal = new BasicUserPrincipal("test");
		mockHttpServletRequest.setUserPrincipal(principal);

		ResponseEntity<Void> response = logoutController.logout(mockHttpServletRequest);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}