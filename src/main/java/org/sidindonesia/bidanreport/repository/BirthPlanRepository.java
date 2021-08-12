package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.BirthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirthPlanRepository extends JpaRepository<BirthPlan, Long> {
}
