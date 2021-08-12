package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.FamilyPlanningEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPlanningEditRepository extends JpaRepository<FamilyPlanningEdit, Long> {
}
