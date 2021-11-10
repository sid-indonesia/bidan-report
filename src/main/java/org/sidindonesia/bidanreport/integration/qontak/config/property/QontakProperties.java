package org.sidindonesia.bidanreport.integration.qontak.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "qontak")
public class QontakProperties {
	private WhatsApp whatsApp;
	private String accessToken;
	private String refreshToken;
	private String tokenType;

	@Data
	public static class WhatsApp {
		private String baseUrl;
		private String clientId;
		private String clientSecret;
		private String username;
		private String password;
		private String pregnantWomanMessageTemplateId;
		private String nonPregnantWomanMessageTemplateId;
		private String visitReminderMessageTemplateId;
		private Integer visitReminderIntervalInDays;
		private Integer visitIntervalInDays;
		private String pregnancyGapMessageTemplateId;
		private Integer pregnancyGapIntervalInDays;
		private String channelIntegrationId;
		private String districtHealthOfficeName;
		private String apiPathBroadcastDirect;
		private String apiPathAuthentication;
	}
}
