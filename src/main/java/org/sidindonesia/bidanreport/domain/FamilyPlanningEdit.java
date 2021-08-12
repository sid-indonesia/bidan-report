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
@Table(name = "family_planning_edit", indexes = {
		@Index(name = "family_planning_edit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FamilyPlanningEdit implements Serializable {

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
	@Column(name = "mother_base_entity_id", length = 36)
	private String motherBaseEntityId;
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
	@Column(name = "checkup_date")
	private LocalDate checkupDate;
	private String parity;
	@Column(name = "visit_date")
	private LocalDate visitDate;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "mid_upper_arm_circumference_in_cm")
	private String midUpperArmCircumferenceInCm;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "birth_date")
	private LocalDate birthDate;
	private String age;
	@Column(name = "systolic_blood_pressure")
	private String systolicBloodPressure;
	@Column(name = "chronic_disease")
	private String chronicDisease;
	private String district;
	@Column(name = "family_planning_status")
	private String familyPlanningStatus;
	@Column(name = "contraception_type")
	private String contraceptionType;
	private String village;
	@Column(name = "checkup_location")
	private String checkupLocation;
	private String province;
	@Column(name = "diastolic_blood_pressure")
	private String diastolicBloodPressure;
	@Column(name = "hb_level")
	private String hbLevel;
}
