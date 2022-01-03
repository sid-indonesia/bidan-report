package org.sidindonesia.bidanreport.integration.qontak.domain.pk;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AutomatedMessageStatsId implements Serializable {
	private String messageTemplateId;
	private LocalDate executionDate;
}
