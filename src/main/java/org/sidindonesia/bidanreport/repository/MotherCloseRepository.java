package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.MotherClose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherCloseRepository extends JpaRepository<MotherClose, Long> {
}
