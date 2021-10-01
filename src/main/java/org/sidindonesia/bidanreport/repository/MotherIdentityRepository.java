package org.sidindonesia.bidanreport.repository;

import java.util.List;
import java.util.Optional;

import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.repository.constant.QueryConstants;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherIdentityRepository extends BaseRepository<MotherIdentity, Long> {
	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES
		+ " AND mi_id_only.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)) "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
		Long lastEventId);

	@Query(nativeQuery = true, value = "SELECT mi.event_id FROM {h-schema}mother_identity mi "
		+ "WHERE mi.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY mi.event_id DESC LIMIT 1")
	Optional<Long> findFirstPregnantWomanByOrderByEventIdDesc();

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES
		+ " AND mi_id_only.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)) "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllNonPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
		Long nonPregnantMotherLastId);

	@Query(nativeQuery = true, value = "SELECT mi.event_id FROM {h-schema}mother_identity mi "
		+ "WHERE mi.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY mi.event_id DESC LIMIT 1")
	Optional<Long> findFirstNonPregnantWomanByOrderByEventIdDesc();
}
