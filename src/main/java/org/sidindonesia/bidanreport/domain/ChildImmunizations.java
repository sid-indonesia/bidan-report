package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity
@Table(name = "child_immunizations", indexes = {
		@Index(name = "child_immunizations_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ChildImmunizations implements Serializable {

	/**
	 * The optimistic lock. Available via standard bean get/set operations.
	 */
	@Version
	@Column(name = "LOCK_FLAG")
	private Integer lockFlag;

	@Column(name = "event_id", unique = true, nullable = false, precision = 19)
	private long eventId;
	@Column(name = "source_date_deleted")
	private LocalDateTime sourceDateDeleted;
	@Column(name = "document_id", length = 36)
	private String documentId;
	@Column(name = "child_base_entity_id", length = 36)
	private String childBaseEntityId;
	@Column(name = "location_id", length = 36)
	private String locationId;
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	@Column(name = "event_date")
	private LocalDateTime eventDate;
	@Column(name = "client_version_epoch")
	private String clientVersionEpoch;
	@Column(name = "server_version_epoch")
	private String serverVersionEpoch;
	@Column(name = "provider_id")
	private String providerId;
	@Column(name = "form_submission_id", length = 36)
	private String formSubmissionId;
	@Column(name = "hb0_vaccine_date")
	private LocalDate hb0VaccineDate;
	@Column(name = "bcg_vaccine_date")
	private LocalDate bcgVaccineDate;
	@Column(name = "polio4_vaccine_date")
	private LocalDate polio4VaccineDate;
	@Column(name = "measles_vaccine_date")
	private LocalDate measlesVaccineDate;
	@Column(name = "polio2_vaccine_date")
	private LocalDate polio2VaccineDate;
	@Column(name = "dpt_hb2_vaccine_date")
	private LocalDate dptHb2VaccineDate;
	@Column(name = "polio3_vaccine_date")
	private LocalDate polio3VaccineDate;
	@Column(name = "dpt_hb3_vaccine_date")
	private LocalDate dptHb3VaccineDate;
	@Column(name = "ipv_vaccine_date")
	private LocalDate ipvVaccineDate;
	@Column(name = "dpt_hb_booster_vaccine_date")
	private LocalDate dptHbBoosterVaccineDate;
	private String village;
	@Column(name = "polio1_vaccine_date")
	private LocalDate polio1VaccineDate;
	@Column(name = "dpt_hb1_vaccine_date")
	private LocalDate dptHb1VaccineDate;
	@Column(name = "measles_booster_vaccine_date")
	private LocalDate measlesBoosterVaccineDate;
}
