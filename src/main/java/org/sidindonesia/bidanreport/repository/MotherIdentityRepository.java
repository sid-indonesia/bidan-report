package org.sidindonesia.bidanreport.repository;

import java.util.List;

import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherIdentityRepository extends BaseRepository<MotherIdentity, Long> {
	@Query("SELECT mi.eventId AS eventId, mi.mobilePhoneNumber AS mobilePhoneNumber, "
		+ "(SELECT cm.fullName FROM org.sidindonesia.bidanreport.domain.ClientMother cm WHERE cm.baseEntityId = mi.motherBaseEntityId) AS fullName "
		+ "FROM #{#entityName} mi "
		+ "WHERE mi.eventId > ?1 AND mi.mobilePhoneNumber IS NOT NULL AND mi.providerId NOT LIKE '%demo%' "
		+ "ORDER BY mi.eventId")
	List<MotherIdentityWhatsAppProjection> findAllByEventIdGreaterThanAndMobilePhoneNumberIsNotNullAndProviderIdNotContainingDemoOrderByEventId(
		Long lastEventId);
}
