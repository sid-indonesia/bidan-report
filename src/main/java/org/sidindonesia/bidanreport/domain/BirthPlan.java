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
@Table(name = "birth_plan", indexes = {
		@Index(name = "birth_plan_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BirthPlan implements Serializable {

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
	@Column(name = "birth_plan_visit_date")
	private LocalDate birthPlanVisitDate;
	@Column(name = "birth_center_plan")
	private String birthCenterPlan;
	@Column(name = "has_permanent_house")
	private String hasPermanentHouse;
	@Column(name = "expected_delivery_date")
	private LocalDate expectedDeliveryDate;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "birth_attendant")
	private String birthAttendant;
	private String transportation;
	@Column(name = "anc_id", length = 36)
	private String ancId;
	private String village;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "birth_support_partner_plan")
	private String birthSupportPartnerPlan;
	private String donor;
}
