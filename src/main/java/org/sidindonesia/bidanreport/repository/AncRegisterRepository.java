package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.AncRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AncRegisterRepository extends JpaRepository<AncRegister, Long> {
}
