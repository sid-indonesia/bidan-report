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
@Table(name = "mother_identity", indexes = {
		@Index(name = "mother_identity_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MotherIdentity implements Serializable {

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
	@Column(name = "registration_date")
	private LocalDate registrationDate;
	@Column(name = "mobile_phone_number")
	private String mobilePhoneNumber;
	@Column(name = "checkup_location")
	private String checkupLocation;
	@Column(name = "other_checkup_location")
	private String otherCheckupLocation;
	@Column(name = "patient_area")
	private String patientArea;
	private String education;
	@Column(name = "integrated_healthcare_center")
	private String integratedHealthcareCenter;
	@Column(name = "blood_type")
	private String bloodType;
	@Column(name = "life_insurance")
	private String lifeInsurance;
	@Column(name = "contraception_type")
	private String contraceptionType;
	@Column(name = "is_transferred_patient")
	private String isTransferredPatient;
	@Column(name = "public_health_center")
	private String publicHealthCenter;
	@Column(name = "is_poor_family")
	private String isPoorFamily;
	private String profession;
	@Column(name = "sub_village")
	private String subVillage;
	private String village;
	private String province;
	private String district;
	private String age;
	@Column(name = "transfer_date")
	private LocalDate transferDate;
	private String religion;
}
