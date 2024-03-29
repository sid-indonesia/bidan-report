package org.sidindonesia.bidanreport.integration.qontak.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "qontak")
public class QontakProperties {
	private String baseUrl;
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private String apiPathAuthentication;
	private String apiPathBroadcastDirect;
	private String apiPathBroadcast;
	private String apiPathContactListAsync;
	private String apiPathContactList;
	private String apiPathUploadFile;
	private WhatsApp whatsApp;
	private Long responseTimeoutInMs;
	private Long connectionTimeoutInMs;

	@Data
	public static class WhatsApp {
		private String pregnantWomanMessageTemplateId;
		private String nonPregnantWomanMessageTemplateId;
		private String visitReminderMessageTemplateId;
		private String visitReminderWithHeaderImageMessageTemplateId;
		private Integer visitReminderIntervalInDays;
		private Integer visitIntervalInDays;
		private String pregnancyGapMessageTemplateId;
		private String healthEducationMessageTemplateId;
		private String channelIntegrationId;
		private String districtHealthOfficeName;
		private String healthEducationContactListCsvAbsoluteFileName;
		private String introMessageContactListCsvAbsoluteFileName;
		private String pregnancyGapContactListCsvAbsoluteFileName;
		private String ancVisitReminderContactListCsvAbsoluteFileName;
	}
}
