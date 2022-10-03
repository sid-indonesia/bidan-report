package org.sidindonesia.bidanreport.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "scheduling")
public class SchedulingProperties {
	private Boolean enabled;
	private FixedRateSchedule introMessage;
	private CronSchedule visitReminder;
	private FixedRateSchedule pregnancyGap;
	private CronSchedule healthEducation;
	private RetrySchedule contactList;

	@Data
	public static class FixedRateSchedule {
		private Long fixedRateInMs;
		private Long initialDelayInMs;
	}

	@Data
	public static class CronSchedule {
		private String cron;
		private String zone;
	}

	@Data
	public static class RetrySchedule {
		private Long delayInMs;
		private Long initialDelayInMs;
		private Integer maxNumberOfRetries;
	}
}
