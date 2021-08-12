package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "pnc_register", indexes = {
		@Index(name = "pnc_register_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PncRegister implements Serializable {

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
	@Column(name = "is_child_alive")
	private String isChildAlive;
	@Column(name = "second_stage_labor_date")
	private LocalDate secondStageLaborDate;
	@Column(name = "fourth_stage_labor_bleeding_amount")
	private String fourthStageLaborBleedingAmount;
	private String helper;
	@Column(name = "high_risk_post_partum_infection")
	private String highRiskPostPartumInfection;
	@Column(name = "high_risk_post_partum_maternal_sepsis")
	private String highRiskPostPartumMaternalSepsis;
	@Column(name = "high_risk_post_partum_forceps")
	private String highRiskPostPartumForceps;
	@Column(name = "birth_center_address")
	private String birthCenterAddress;
	@Column(name = "birth_method")
	private String birthMethod;
	@Column(name = "high_risk_post_partum_dystocia")
	private String highRiskPostPartumDystocia;
	@Column(name = "is_given_treatment")
	private String isGivenTreatment;
	@Column(name = "third_stage_labor_active_management")
	private String thirdStageLaborActiveManagement;
	@Column(name = "high_risk_post_partum_pregnancy_induced_hypertension")
	private String highRiskPostPartumPregnancyInducedHypertension;
	@Column(name = "high_risk_post_partum_vacuum")
	private String highRiskPostPartumVacuum;
	@Column(name = "active_first_stage_labor_date")
	private LocalDate activeFirstStageLaborDate;
	@Column(name = "active_first_stage_labor_time")
	private LocalTime activeFirstStageLaborTime;
	private String treatment;
	@Column(name = "active_second_stage_labor_time")
	private LocalTime activeSecondStageLaborTime;
	@Column(name = "placenta_delivery_time")
	private LocalTime placentaDeliveryTime;
	@Column(name = "fetal_presentation")
	private String fetalPresentation;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "last_menstrual_period_date")
	private LocalDate lastMenstrualPeriodDate;
	@Column(name = "is_mother_alive")
	private String isMotherAlive;
	@Column(name = "labor_stages")
	private String laborStages;
	@Column(name = "high_risk_post_partum_pre_eclampsia")
	private String highRiskPostPartumPreEclampsia;
	@Column(name = "birth_center")
	private String birthCenter;
	@Column(name = "referral_facility")
	private String referralFacility;
	@Column(name = "high_risk_post_partum_hemorrhage")
	private String highRiskPostPartumHemorrhage;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	private String village;
	private String complications;
	@Column(name = "placenta_delivery_date")
	private LocalDate placentaDeliveryDate;
	@Column(name = "program_integration")
	private String programIntegration;
}
