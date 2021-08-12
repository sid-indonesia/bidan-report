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

import lombok.Data;

@Data
@Entity
@Table(name = "anc_register", indexes = {
		@Index(name = "anc_register_event_id_IX", columnList = "event_id", unique = true) })
public class AncRegister implements Serializable {

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
	@Column(name = "previous_delivery")
	private String previousDelivery;
	@Column(name = "high_risk_kidney_disorder")
	private String highRiskKidneyDisorder;
	private String allergy;
	@Column(name = "expected_delivery_date")
	private LocalDate expectedDeliveryDate;
	@Column(name = "height_in_cm")
	private String heightInCm;
	@Column(name = "high_risk_labour_mother_height")
	private String highRiskLabourMotherHeight;
	@Column(name = "high_risk_hiv_aids")
	private String highRiskHivAids;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	private String abortus;
	@Column(name = "weight_in_kg")
	private String weightInKg;
	@Column(name = "high_risk_tuberculosis")
	private String highRiskTuberculosis;
	private String type;
	@Column(name = "obstetric_complications_history")
	private String obstetricComplicationsHistory;
	@Column(name = "pregnancy_trimester")
	private String pregnancyTrimester;
	private String village;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "number_of_living_children")
	private String numberOfLivingChildren;
	@Column(name = "chronic_disease")
	private String chronicDisease;
	@Column(name = "high_risk_pregnancy_abortus")
	private String highRiskPregnancyAbortus;
	@Column(name = "high_risk_heart_disorder")
	private String highRiskHeartDisorder;
	@Column(name = "sub_village")
	private String subVillage;
	private String partus;
	@Column(name = "high_risk_asthma")
	private String highRiskAsthma;
	private String province;
	private String district;
	@Column(name = "last_menstrual_period_date")
	private LocalDate lastMenstrualPeriodDate;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "gestational_age")
	private String gestationalAge;
	@Column(name = "mid_upper_arm_circumference_in_cm")
	private String midUpperArmCircumferenceInCm;
	@Column(name = "high_risk_labour_section_cesarea_record")
	private String highRiskLabourSectionCesareaRecord;
	@Column(name = "high_risk_malaria")
	private String highRiskMalaria;
	@Column(name = "high_risk_cardiovascular_disease_record")
	private String highRiskCardiovascularDiseaseRecord;
	@Column(name = "high_risk_pregnancy_protein_energy_malnutrition")
	private String highRiskPregnancyProteinEnergyMalnutrition;
	@Column(name = "high_risk_pregnancy_too_many_children")
	private String highRiskPregnancyTooManyChildren;
	@Column(name = "pregnancy_test")
	private String pregnancyTest;
	@Column(name = "reference_date")
	private LocalDate referenceDate;
	private String gravida;
	@Column(name = "previous_child_birth_date")
	private LocalDate previousChildBirthDate;
	@Column(name = "maternal_and_child_health_book_receiving_date")
	private LocalDate maternalAndChildHealthBookReceivingDate;
	@Column(name = "high_risk_ectopic_pregnancy")
	private String highRiskEctopicPregnancy;
}
