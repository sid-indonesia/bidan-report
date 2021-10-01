package org.sidindonesia.bidanreport.repository;

import java.util.List;
import java.util.Optional;

import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherIdentityRepository extends BaseRepository<MotherIdentity, Long> {
	@Query(nativeQuery = true, value = "SELECT mi.event_id AS eventId, mi.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = mi.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_identity mi "
		+ "WHERE mi.event_id IN (SELECT MAX(mi_id_only.event_id) OVER (PARTITION BY mi_id_only.mobile_phone_number)"
		+ " FROM {h-schema}mother_identity mi_id_only"
		+ " WHERE mi_id_only.event_id > ?1 AND mi_id_only.mobile_phone_number IS NOT NULL AND mi_id_only.provider_id NOT LIKE '%demo%'"
		+ " AND mi_id_only.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)) "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
		Long lastEventId);

	@Query(nativeQuery = true, value = "SELECT mi.event_id FROM {h-schema}mother_identity mi "
		+ "WHERE mi.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY mi.event_id DESC LIMIT 1")
	Optional<Long> findFirstPregnantWomanByOrderByEventIdDesc();
}
