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
@Table(name = "anc_visit", indexes = { @Index(name = "anc_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AncVisit implements Serializable {

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
	@Column(name = "anc_date")
	private LocalDate ancDate;
	@Column(name = "additional_referral_information")
	private String additionalReferralInformation;
	@Column(name = "high_risk_labour_fetus_number")
	private String highRiskLabourFetusNumber;
	@Column(name = "mobile_phone_number")
	private String mobilePhoneNumber;
	@Column(name = "uterine_fundal_height")
	private String uterineFundalHeight;
	@Column(name = "fetal_presentation")
	private String fetalPresentation;
	@Column(name = "hidden_anc_visit_category")
	private String hiddenAncVisitCategory;
	@Column(name = "is_following_who_four_visit_focused_anc")
	private String isFollowingWhoFourVisitFocusedAnc;
	@Column(name = "fetal_heart_rate")
	private String fetalHeartRate;
	@Column(name = "number_of_fetus")
	private String numberOfFetus;
	@Column(name = "referral_facility")
	private String referralFacility;
	@Column(name = "vital_sign_diastolic_blood_pressure")
	private String vitalSignDiastolicBloodPressure;
	@Column(name = "mid_upper_arm_circumference_in_cm")
	private String midUpperArmCircumferenceInCm;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	@Column(name = "gestational_age")
	private String gestationalAge;
	@Column(name = "is_community_health_insurance")
	private String isCommunityHealthInsurance;
	@Column(name = "process_to_access_health_facility")
	private String processToAccessHealthFacility;
	@Column(name = "has_telephone_number")
	private String hasTelephoneNumber;
	@Column(name = "mother_anamnesis")
	private String motherAnamnesis;
	@Column(name = "complications_during_pregnancy")
	private String complicationsDuringPregnancy;
	private String village;
	@Column(name = "other_complications")
	private String otherComplications;
	@Column(name = "anc_visit_number")
	private String ancVisitNumber;
	@Column(name = "anc_visit_category")
	private String ancVisitCategory;
	@Column(name = "mother_condition_when_discharged_from_referral_facility")
	private String motherConditionWhenDischargedFromReferralFacility;
	@Column(name = "pregnancy_trimester")
	private String pregnancyTrimester;
	@Column(name = "high_risk_labour_fetus_malpresentation")
	private String highRiskLabourFetusMalpresentation;
	@Column(name = "weight_in_kg")
	private String weightInKg;
	@Column(name = "vital_sign_systolic_blood_pressure")
	private String vitalSignSystolicBloodPressure;
	@Column(name = "is_given_iron_folic_acid_tablet")
	private String isGivenIronFolicAcidTablet;
	@Column(name = "iron_folic_acid_tablet_amount")
	private String ironFolicAcidTabletAmount;
	private String province;
	@Column(name = "tetanus_toxoid_immunization_status")
	private String tetanusToxoidImmunizationStatus;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "mother_condition_when_arrived_at_referral_facility")
	private String motherConditionWhenArrivedAtReferralFacility;
	@Column(name = "is_given_tetanus_toxoid_injection")
	private String isGivenTetanusToxoidInjection;
	@Column(name = "is_recorded_in_maternal_and_child_health_book")
	private String isRecordedInMaternalAndChildHealthBook;
	@Column(name = "is_given_treatment")
	private String isGivenTreatment;
	@Column(name = "has_patella_reflex")
	private String hasPatellaReflex;
	@Column(name = "estimated_fetal_weight")
	private String estimatedFetalWeight;
	private String treatment;
	@Column(name = "high_risk_pregnancy_protein_energy_malnutrition")
	private String highRiskPregnancyProteinEnergyMalnutrition;
	@Column(name = "high_risk_pregnancy_induced_hypertension")
	private String highRiskPregnancyInducedHypertension;
	private String district;
	@Column(name = "maternal_nutritional_status")
	private String maternalNutritionalStatus;
	@Column(name = "high_risk_labour_fetus_size")
	private String highRiskLabourFetusSize;
	@Column(name = "risk_first_detected_by")
	private String riskFirstDetectedBy;
	@Column(name = "iron_folic_acid_tablet_giving_type")
	private String ironFolicAcidTabletGivingType;
}
