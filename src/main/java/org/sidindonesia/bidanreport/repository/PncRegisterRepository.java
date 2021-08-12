package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.PncRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PncRegisterRepository extends JpaRepository<PncRegister, Long> {
}
