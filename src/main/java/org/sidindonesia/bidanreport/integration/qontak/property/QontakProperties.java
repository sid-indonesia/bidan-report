package org.sidindonesia.bidanreport.integration.qontak.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "qontak")
public class QontakProperties {
	private WhatsApp whatsApp;

	@Data
	public static class WhatsApp {
		private String baseUrl;
		private String clientId;
		private String clientSecret;
		private String username;
		private String password;
		private String accessToken;
		private String messageTemplateId;
		private String channelIntegrationId;
	}
}
