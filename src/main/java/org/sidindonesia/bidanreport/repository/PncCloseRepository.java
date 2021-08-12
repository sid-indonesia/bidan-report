package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.PncClose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PncCloseRepository extends JpaRepository<PncClose, Long> {
}
