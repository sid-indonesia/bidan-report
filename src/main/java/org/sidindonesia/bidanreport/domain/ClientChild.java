package org.sidindonesia.bidanreport.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

@Data
@Entity(name = "client_child")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClientChild implements Serializable {

	/**
	 * The optimistic lock. Available via standard bean get/set operations.
	 */
	@Version
	@Column(name = "LOCK_FLAG")
	private Integer lockFlag;

	@Column(name = "source_id", unique = true, nullable = false, precision = 19)
	private long sourceId;
	@Column(name = "source_date_deleted")
	private LocalDateTime sourceDateDeleted;
	@Column(name = "document_id", length = 36)
	private String documentId;
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	@Column(name = "base_entity_id", length = 36)
	private String baseEntityId;
	@Column(name = "full_name")
	private String fullName;
	@Column(name = "birth_date")
	private LocalDateTime birthDate;
	@Column(name = "server_version_epoch")
	private String serverVersionEpoch;
	@Column(name = "mother_base_entity_id", length = 36)
	private String motherBaseEntityId;
	@Column(name = "openmrs_uuid", length = 36)
	private String openmrsUuid;
	private String gender;
	@Column(name = "date_edited")
	private LocalDateTime dateEdited;
}
