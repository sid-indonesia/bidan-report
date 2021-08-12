package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity
@Table(name = "infant_visit", indexes = {
		@Index(name = "infant_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InfantVisit implements Serializable {

	/**
	 * The optimistic lock. Available via standard bean get/set operations.
	 */
	@Version
	@Column(name = "LOCK_FLAG")
	private Integer lockFlag;

	@Id
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
	@Column(name = "baby_weight_monitoring_indicator")
	private String babyWeightMonitoringIndicator;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "child_length")
	private String childLength;
	@Column(name = "referral_facility")
	private String referralFacility;
	private String disease;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "child_visit_number")
	private String childVisitNumber;
	@Column(name = "child_nutritional_status")
	private String childNutritionalStatus;
	@Column(name = "is_given_vitamin_a")
	private String isGivenVitaminA;
	@Column(name = "child_health_status")
	private String childHealthStatus;
	@Column(name = "month_of_exclusive_breastfeeding_given")
	private String monthOfExclusiveBreastfeedingGiven;
	@Column(name = "child_development_pre_screening_result")
	private String childDevelopmentPreScreeningResult;
	@Column(name = "is_given_integrated_management_of_childhood_illness")
	private String isGivenIntegratedManagementOfChildhoodIllness;
	@Column(name = "is_referred")
	private String isReferred;
	private String village;
	@Column(name = "visit_date")
	private LocalDate visitDate;
	@Column(name = "child_age_during_visit")
	private String childAgeDuringVisit;
	@Column(name = "child_weight")
	private String childWeight;
}
