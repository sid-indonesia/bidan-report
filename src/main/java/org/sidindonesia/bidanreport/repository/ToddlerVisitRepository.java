package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.ToddlerVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToddlerVisitRepository extends JpaRepository<ToddlerVisit, Long> {
}
