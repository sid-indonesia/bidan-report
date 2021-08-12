package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.IntegratedAncVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratedAncVisitRepository extends JpaRepository<IntegratedAncVisit, Long> {
}
