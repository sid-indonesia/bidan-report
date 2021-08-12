package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity(name = "client_mother")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClientMother implements Serializable {

	@Column(name = "source_id", unique = false, nullable = false, precision = 19)
	private long sourceId;
	@Column(name = "source_date_deleted")
	private LocalDateTime sourceDateDeleted;
	@Column(name = "document_id", length = 36)
	private String documentId;
	@Id
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	@Column(name = "base_entity_id", length = 36)
	private String baseEntityId;
	@Column(name = "full_name")
	private String fullName;
	@Column(name = "husband_name")
	private String husbandName;
	private String province;
	private String district;
	private String village;
	private String address;
	private String gps;
	@Column(name = "birth_date")
	private LocalDateTime birthDate;
	private String nik;
	@Column(name = "mother_registration_number")
	private String motherRegistrationNumber;
	@Column(name = "anc_id")
	private String ancId;
	@Column(name = "traditional_birth_attendant")
	private String traditionalBirthAttendant;
	@Column(name = "community_health_worker_name")
	private String communityHealthWorkerName;
	@Column(name = "server_version_epoch")
	private String serverVersionEpoch;
}
