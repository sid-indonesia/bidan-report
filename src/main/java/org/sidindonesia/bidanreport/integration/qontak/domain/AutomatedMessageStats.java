package org.sidindonesia.bidanreport.integration.qontak.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity
@Table(name = "automated_message_stats")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AutomatedMessageStats implements Serializable {

	@Id
	@Column(name = "message_template_id", unique = true, nullable = false, length = 36)
	private String messageTemplateId;
	@Column(name = "message_template_name")
	private String messageTemplateName;
	@Column(name = "successful_attempts")
	private Long successfulAttempts;
	@Column(name = "failed_attempts")
	private Long failedAttempts;
}
