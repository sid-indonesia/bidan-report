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
@Table(name = "integrated_anc_visit", indexes = {
		@Index(name = "integrated_anc_visit_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class IntegratedAncVisit implements Serializable {

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
	@Column(name = "high_risk_tuberculosis")
	private String highRiskTuberculosis;
	@Column(name = "program_integration_malaria_blood_check")
	private String programIntegrationMalariaBloodCheck;
	@Column(name = "program_integration_malaria_medication")
	private String programIntegrationMalariaMedication;
	@Column(name = "program_integration_has_mosquito_net")
	private String programIntegrationHasMosquitoNet;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "high_risk_malaria")
	private String highRiskMalaria;
	@Column(name = "program_integration_tuberculosis_phlegm")
	private String programIntegrationTuberculosisPhlegm;
	@Column(name = "program_integration_tuberculosis_medication")
	private String programIntegrationTuberculosisMedication;
	@Column(name = "program_integration_pmtct_arv_prophylaxis")
	private String programIntegrationPmtctArvProphylaxis;
	private String village;
	@Column(name = "reference_date")
	private LocalDate referenceDate;
	@Column(name = "program_integration_pmtct_is_voluntary_counseling_test")
	private String programIntegrationPmtctIsVoluntaryCounselingTest;
	@Column(name = "program_integration_pmtct_blood_check")
	private String programIntegrationPmtctBloodCheck;
	@Column(name = "program_integration_pmtct_serology")
	private String programIntegrationPmtctSerology;
}
