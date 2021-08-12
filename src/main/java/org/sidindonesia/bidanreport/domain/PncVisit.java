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
@Table(name = "pnc_visit", indexes = { @Index(name = "pnc_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PncVisit implements Serializable {

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
	@Column(name = "high_risk_post_partum_infection")
	private String highRiskPostPartumInfection;
	@Column(name = "high_risk_post_partum_pregnancy_induced_hypertension")
	private String highRiskPostPartumPregnancyInducedHypertension;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "program_integration_antimalarial_drug")
	private String programIntegrationAntimalarialDrug;
	@Column(name = "other_complication")
	private String otherComplication;
	@Column(name = "is_given_treatment")
	private String isGivenTreatment;
	@Column(name = "high_risk_post_partum_maternal_sepsis")
	private String highRiskPostPartumMaternalSepsis;
	private String village;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	private String treatment;
	@Column(name = "is_given_chest_xray")
	private String isGivenChestXray;
	private String complications;
	@Column(name = "vital_sign_diastolic_blood_pressure")
	private String vitalSignDiastolicBloodPressure;
	@Column(name = "vital_sign_mother_body_temperature")
	private String vitalSignMotherBodyTemperature;
	@Column(name = "is_given_2_hours_postpartum_vitamin_a")
	private String isGiven2HoursPostpartumVitaminA;
	@Column(name = "is_referred")
	private String isReferred;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	@Column(name = "pnc_date")
	private LocalDate pncDate;
	@Column(name = "pnc_visit_number")
	private String pncVisitNumber;
	@Column(name = "program_integration_antituberculosis_drug")
	private String programIntegrationAntituberculosisDrug;
	@Column(name = "high_risk_post_partum_hemorrhage")
	private String highRiskPostPartumHemorrhage;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "high_risk_post_partum_mastitis")
	private String highRiskPostPartumMastitis;
	@Column(name = "vital_sign_systolic_blood_pressure")
	private String vitalSignSystolicBloodPressure;
	@Column(name = "is_given_iron_folic_acid_tablet")
	private String isGivenIronFolicAcidTablet;
	@Column(name = "is_given_24_hours_postpartum_vitamin_a")
	private String isGiven24HoursPostpartumVitaminA;
	@Column(name = "referral_facility")
	private String referralFacility;
	@Column(name = "additional_referral_information")
	private String additionalReferralInformation;
	@Column(name = "is_recorded_in_maternal_and_child_health_book")
	private String isRecordedInMaternalAndChildHealthBook;
	@Column(name = "vital_sign_pulse_rate")
	private String vitalSignPulseRate;
	@Column(name = "vital_sign_respiratory_rate")
	private String vitalSignRespiratoryRate;
	@Column(name = "lochia_stage")
	private String lochiaStage;
	@Column(name = "general_condition")
	private String generalCondition;
	@Column(name = "bleeding_amount")
	private String bleedingAmount;
}
