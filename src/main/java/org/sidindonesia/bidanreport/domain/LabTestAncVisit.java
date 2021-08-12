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
@Table(name = "lab_test_anc_visit", indexes = {
		@Index(name = "lab_test_anc_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LabTestAncVisit implements Serializable {

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
	@Column(name = "high_risk_pregnancy_diabetes")
	private String highRiskPregnancyDiabetes;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "treatment_diabetes")
	private String treatmentDiabetes;
	@Column(name = "reference_date")
	private LocalDate referenceDate;
	@Column(name = "has_proteinuria")
	private String hasProteinuria;
	@Column(name = "has_anemia")
	private String hasAnemia;
	@Column(name = "has_thalasemia")
	private String hasThalasemia;
	@Column(name = "high_risk_pregnancy_anemia")
	private String highRiskPregnancyAnemia;
	@Column(name = "treatment_anemia")
	private String treatmentAnemia;
	@Column(name = "treatment_thalasemia")
	private String treatmentThalasemia;
	@Column(name = "treatment_syphilis")
	private String treatmentSyphilis;
	@Column(name = "treatment_hbsag")
	private String treatmentHbsag;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "treatment_proteinuria")
	private String treatmentProteinuria;
	@Column(name = "has_syphilis")
	private String hasSyphilis;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "hb_level_lab_test_result")
	private String hbLevelLabTestResult;
	@Column(name = "is_glucose_blood_more_than_140_mg_dl")
	private String isGlucoseBloodMoreThan140MgDl;
	@Column(name = "has_hbsag")
	private String hasHbsag;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	private String village;
}
