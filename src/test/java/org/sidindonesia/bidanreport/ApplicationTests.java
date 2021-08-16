package org.sidindonesia.bidanreport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@Disabled
class ApplicationTests {

	@Autowired
	public Application application;

	@Test
	void contextLoads() {
		assertThat(application).isNotNull();
	}
}
