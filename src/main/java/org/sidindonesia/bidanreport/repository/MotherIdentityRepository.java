package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherIdentityRepository extends JpaRepository<MotherIdentity, Long> {
}
