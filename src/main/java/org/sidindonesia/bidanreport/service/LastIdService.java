package org.sidindonesia.bidanreport.service;

import java.util.Optional;

import org.sidindonesia.bidanreport.config.property.LastIdProperties;
import org.sidindonesia.bidanreport.repository.AncVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LastIdService {
	private final LastIdProperties lastIdProperties;
	private final AncVisitRepository ancVisitRepository;

	public void syncANCVisitPregnancyGapLastId() {
		Optional<Long> optAncVisitPregnancyGapLastId = ancVisitRepository.findLastEventId();
		if (optAncVisitPregnancyGapLastId.isPresent()) {
			lastIdProperties.setAncVisitPregnancyGapLastId(optAncVisitPregnancyGapLastId.get());
		}
	}
}
