package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity
@Table(name = "neonatal_visit", indexes = {
		@Index(name = "neonatal_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NeonatalVisit implements Serializable {

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
	@Column(name = "neonatal_visit_date")
	private LocalDate neonatalVisitDate;
	@Column(name = "neonatal_complications")
	private String neonatalComplications;
	@Column(name = "is_neonatal_complications_handled")
	private String isNeonatalComplicationsHandled;
	@Column(name = "high_risk_child_hypothermia")
	private String highRiskChildHypothermia;
	@Column(name = "high_risk_child_congenital_abnormality")
	private String highRiskChildCongenitalAbnormality;
	private String village;
	@Column(name = "child_condition")
	private String childCondition;
	@Column(name = "newborn_services")
	private String newbornServices;
	@Column(name = "child_respiration_rate")
	private String childRespirationRate;
	@Column(name = "child_heart_rate")
	private String childHeartRate;
	@Column(name = "neonatal_visit_type")
	private String neonatalVisitType;
	@Column(name = "high_risk_child_asphyxia")
	private String highRiskChildAsphyxia;
	@Column(name = "high_risk_child_birth_trauma")
	private String highRiskChildBirthTrauma;
	@Column(name = "high_risk_child_neonatal_sepsis")
	private String highRiskChildNeonatalSepsis;
	@Column(name = "child_length")
	private String childLength;
	@Column(name = "is_referred")
	private String isReferred;
	@Column(name = "high_risk_child_tetanus_neonatorum")
	private String highRiskChildTetanusNeonatorum;
	@Column(name = "child_body_temperature")
	private String childBodyTemperature;
	@Column(name = "high_risk_child_icterus")
	private String highRiskChildIcterus;
	@Column(name = "high_risk_child_infection")
	private String highRiskChildInfection;
	@Column(name = "child_weight")
	private String childWeight;
	@Column(name = "hb0_vaccine_date")
	private LocalDate hb0VaccineDate;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "neonatal_checklist")
	private String neonatalChecklist;
}
