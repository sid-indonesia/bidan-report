package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.PostpartumFamilyPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostpartumFamilyPlanningRepository extends JpaRepository<PostpartumFamilyPlanning, Long> {
}
