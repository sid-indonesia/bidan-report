package org.sidindonesia.bidanreport.repository;

import java.util.List;
import java.util.Optional;

import org.sidindonesia.bidanreport.domain.MotherEdit;
import org.sidindonesia.bidanreport.repository.constant.QueryConstants;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherEditRepository extends BaseRepository<MotherEdit, Long> {
	@Query(nativeQuery = true, value = QueryConstants.MOTHER_EDIT_NATIVE_QUERY_FIND_LAST_EDIT_AND_IN_MOTHER_IDENTITY_NO_MOBILE_PHONE_NUMBER
		+ " AND me_id_only.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)"
		+ ") ORDER BY me.event_id")
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
		Long motherEditLastId);

	@Query(nativeQuery = true, value = "SELECT me.event_id FROM {h-schema}mother_edit me "
		+ "WHERE me.mother_base_entity_id IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY me.event_id DESC LIMIT 1")
	Optional<Long> findFirstPregnantWomanByOrderByEventIdDesc();

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_EDIT_NATIVE_QUERY_FIND_LAST_EDIT_AND_IN_MOTHER_IDENTITY_NO_MOBILE_PHONE_NUMBER
		+ " AND me_id_only.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)"
		+ ") ORDER BY me.event_id")
	List<MotherIdentityWhatsAppProjection> findAllNonPregnantWomenByLastEditAndPreviouslyInMotherIdentityNoMobilePhoneNumberOrderByEventId(
		Long nonPregnantMotherLastId);

	@Query(nativeQuery = true, value = "SELECT me.event_id FROM {h-schema}mother_edit me "
		+ "WHERE me.mother_base_entity_id NOT IN (SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar) "
		+ "ORDER BY me.event_id DESC LIMIT 1")
	Optional<Long> findFirstNonPregnantWomanByOrderByEventIdDesc();

	@Query(nativeQuery = true, value = QueryConstants.MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_SOME_DAYS_AGO)
	List<MotherIdentityWhatsAppProjection> findAllPregnantWomenToBeRemindedForTheNextANCVisit(
		Integer numberOfDaysBeforeNextVisit);
}
