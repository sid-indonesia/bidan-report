package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.LabTestAncVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestAncVisitRepository extends JpaRepository<LabTestAncVisit, Long> {
}
