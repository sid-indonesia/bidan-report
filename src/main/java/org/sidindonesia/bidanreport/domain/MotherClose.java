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
@Table(name = "mother_close", indexes = {
		@Index(name = "mother_close_event_id_IX", columnList = "event_id", unique = true) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MotherClose implements Serializable {

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
	private String village;
	@Column(name = "other_close_reason")
	private String otherCloseReason;
	@Column(name = "is_confirmed")
	private String isConfirmed;
	@Column(name = "close_reason")
	private String closeReason;
	@Column(name = "sub_village")
	private String subVillage;
	@Column(name = "maternal_death_date")
	private LocalDate maternalDeathDate;
	@Column(name = "place_of_death")
	private String placeOfDeath;
	@Column(name = "maternal_death_cause")
	private String maternalDeathCause;
	@Column(name = "birth_attendant")
	private String birthAttendant;
	@Column(name = "is_referred")
	private String isReferred;
	@Column(name = "referral_location")
	private String referralLocation;
}
