package org.sidindonesia.bidanreport.repository;

import java.util.List;
import java.util.Optional;

import org.sidindonesia.bidanreport.domain.MotherIdentity;
import org.sidindonesia.bidanreport.repository.constant.QueryConstants;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;
import org.sidindonesia.bidanreport.repository.projection.AncVisitReminderProjection;
import org.sidindonesia.bidanreport.repository.projection.HealthEducationProjection;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherIdentityRepository extends BaseRepository<MotherIdentity, Long> {
	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES
		+ " AND mi_id_only.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)) "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
		Long lastEventId);

	@Query(nativeQuery = true, value = "SELECT mi.event_id FROM {h-schema}mother_identity mi "
		+ "WHERE mi.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY mi.event_id DESC LIMIT 1")
	Optional<Long> findFirstPregnantWomanByOrderByEventIdDesc();

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES
		+ " AND mi_id_only.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)) "
		+ "ORDER BY mi.event_id")
	List<MotherIdentityWhatsAppProjection> findAllNonPregnantWomenByEventIdGreaterThanAndHasMobilePhoneNumberOrderByEventId(
		Long nonPregnantMotherLastId);

	@Query(nativeQuery = true, value = "SELECT mi.event_id FROM {h-schema}mother_identity mi "
		+ "WHERE mi.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY mi.event_id DESC LIMIT 1")
	Optional<Long> findFirstNonPregnantWomanByOrderByEventIdDesc();

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_CURRENT_DATE_MINUS_ANC_VISIT_INTERVAL_IN_DAYS_PLUS_VISIT_REMINDER_INTERVAL_IN_DAYS)
	List<AncVisitReminderProjection> findAllPregnantWomenToBeRemindedForTheNextANCVisit(Integer visitIntervalInDays,
		Integer numberOfDaysBeforeNextVisit);

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_PREGNANCY_GAP)
	List<PregnancyGapProjection> findAllPregnantWomenToBeInformedOfHerGapOnPregnancy(Long lastEventId);

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LAST_MENSTRUAL_PERIOD_DATE_NO_EARLIER_THAN_42_WEEKS_AGO_OR_EXPECTED_DELIVERY_DATE_IS_STILL_IN_THE_FUTURE)
	List<HealthEducationProjection> findAllPregnantWomenToBeGivenEducationOfTheirHealth();
}
