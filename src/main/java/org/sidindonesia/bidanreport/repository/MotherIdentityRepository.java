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
		+ "WHERE cm.base_entity_id = mi.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS full_name "
		+ "FROM {h-schema}mother_identity mi "
		+ "WHERE mi.event_id > ?1 AND mi.mobile_phone_number IS NOT NULL AND mi.provider_id NOT LIKE '%demo%' "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllByEventIdGreaterThanAndMobilePhoneNumberIsNotNullAndProviderIdNotContainingDemoOrderByEventId(
		Long lastEventId);

	Optional<MotherIdentity> findFirstByOrderByEventIdDesc();
}
