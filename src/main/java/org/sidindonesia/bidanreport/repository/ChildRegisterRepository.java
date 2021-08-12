package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.ChildRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRegisterRepository extends JpaRepository<ChildRegister, Long> {
}
