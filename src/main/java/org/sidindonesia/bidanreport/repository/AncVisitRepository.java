package org.sidindonesia.bidanreport.repository;

import java.util.Optional;

import org.sidindonesia.bidanreport.domain.AncVisit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AncVisitRepository extends BaseRepository<AncVisit, Long> {
	@Query(nativeQuery = true, value = "SELECT av.event_id FROM {h-schema}anc_visit av "
		+ "ORDER BY av.event_id DESC LIMIT 1")
	Optional<Long> findLastEventId();
}
