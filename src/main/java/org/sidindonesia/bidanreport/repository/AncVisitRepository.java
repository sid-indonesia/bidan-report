package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.AncVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AncVisitRepository extends JpaRepository<AncVisit, Long> {
}
