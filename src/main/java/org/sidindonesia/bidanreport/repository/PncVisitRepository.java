package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.PncVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PncVisitRepository extends JpaRepository<PncVisit, Long> {
}
