package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.ChildImmunizations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildImmunizationsRepository extends JpaRepository<ChildImmunizations, Long> {
}
