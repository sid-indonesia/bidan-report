package org.sidindonesia.bidanreport.repository;

import java.time.LocalDateTime;

import org.sidindonesia.bidanreport.domain.ClientChild;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientChildRepository extends BaseRepository<ClientChild, LocalDateTime> {
}
