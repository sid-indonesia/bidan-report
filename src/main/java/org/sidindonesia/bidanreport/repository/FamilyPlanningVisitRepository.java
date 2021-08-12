package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.FamilyPlanningVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPlanningVisitRepository extends JpaRepository<FamilyPlanningVisit, Long> {
}
