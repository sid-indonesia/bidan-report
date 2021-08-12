package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.FamilyPlanningRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPlanningRegisterRepository extends JpaRepository<FamilyPlanningRegister, Long> {
}
