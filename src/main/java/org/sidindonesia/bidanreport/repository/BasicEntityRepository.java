package org.sidindonesia.bidanreport.repository;

import org.sidindonesia.bidanreport.domain.BasicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicEntityRepository extends JpaRepository<BasicEntity, String> {
}
