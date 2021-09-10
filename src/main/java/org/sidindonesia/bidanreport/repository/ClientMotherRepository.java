package org.sidindonesia.bidanreport.repository;

import java.time.LocalDateTime;

import org.sidindonesia.bidanreport.domain.ClientMother;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientMotherRepository extends BaseRepository<ClientMother, LocalDateTime> {
}
