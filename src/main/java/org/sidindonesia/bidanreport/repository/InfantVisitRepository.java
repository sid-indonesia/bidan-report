package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.InfantVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfantVisitRepository extends JpaRepository<InfantVisit, Long> {
}
