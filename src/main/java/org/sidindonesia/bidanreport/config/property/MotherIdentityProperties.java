package org.sidindonesia.bidanreport.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "mother-identity")
public class MotherIdentityProperties {
	private Long lastId = 0L;
}
