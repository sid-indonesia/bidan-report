package org.sidindonesia.bidanreport.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, I> extends JpaRepository<T, I> {
	List<T> findAllByDateCreatedBetween(LocalDateTime fromDate, LocalDateTime untilDate);
}
