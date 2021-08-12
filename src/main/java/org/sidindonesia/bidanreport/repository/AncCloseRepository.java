package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.AncClose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AncCloseRepository extends JpaRepository<AncClose, Long> {
}
