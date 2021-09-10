package org.sidindonesia.bidanreport.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
	List<T> findAllByDateCreatedBetween(LocalDateTime fromDate, LocalDateTime untilDate);
}
