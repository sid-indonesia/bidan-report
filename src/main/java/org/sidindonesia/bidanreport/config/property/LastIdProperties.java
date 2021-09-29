package org.sidindonesia.bidanreport.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "last-id")
public class LastIdProperties {
	private Long motherIdentityLastId = 0L;
	private Long motherEditLastId = 0L;
}
