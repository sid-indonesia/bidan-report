package org.sidindonesia.bidanreport.repository;

import java.util.List;
import java.util.Optional;

import org.sidindonesia.bidanreport.domain.MotherEdit;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherEditRepository extends BaseRepository<MotherEdit, Long> {
	@Query(nativeQuery = true, value = "SELECT me.event_id AS eventId, me.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = me.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_edit me "
		+ "WHERE me.event_id IN (SELECT MAX(me_id_only.event_id) OVER (PARTITION BY me_id_only.mother_base_entity_id)"
		+ " FROM {h-schema}mother_edit me_id_only WHERE me_id_only.event_id > ?1"
		+ " AND me_id_only.mobile_phone_number IS NOT NULL AND me_id_only.provider_id NOT LIKE '%demo%'"
		+ " AND me_id_only.mother_base_entity_id IN (SELECT mi.mother_base_entity_id"
		+ "  FROM {h-schema}mother_identity mi WHERE mi.mobile_phone_number IS NULL)"
		+ " AND me_id_only.mother_base_entity_id NOT IN (SELECT me_duplicate.mother_base_entity_id"
		+ "  FROM {h-schema}mother_edit me_duplicate WHERE me_duplicate.mobile_phone_number IS NOT NULL"
		+ "  AND me_duplicate.event_id <= ?1 AND me_duplicate.provider_id NOT LIKE '%demo%')"
		+ " AND me_id_only.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)"
		+ ") ORDER BY me.event_id")
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
		Long motherEditLastId);

	@Query(nativeQuery = true, value = "SELECT me.event_id FROM {h-schema}mother_edit me "
		+ "WHERE me.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY me.event_id DESC LIMIT 1")
	Optional<Long> findFirstPregnantWomanByOrderByEventIdDesc();
}
