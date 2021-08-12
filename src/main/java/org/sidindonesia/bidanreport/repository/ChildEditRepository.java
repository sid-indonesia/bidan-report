package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.ChildEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildEditRepository extends JpaRepository<ChildEdit, Long> {
}
