package org.sidindonesia.bidanreport.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "last-id")
public class LastIdProperties {
	private LastId motherIdentity = new LastId();
	private LastId motherEdit = new LastId();

	@Data
	public static class LastId {
		private Long pregnantMotherLastId = 0L;
		private Long nonPregnantMotherLastId = 0L;
	}
}
