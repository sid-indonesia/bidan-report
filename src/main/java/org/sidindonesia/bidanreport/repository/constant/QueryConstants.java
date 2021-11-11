package org.sidindonesia.bidanreport.repository.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryConstants {

	private static final String INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE = "  INNER JOIN (SELECT mother_base_entity_id, MAX(anc_date) AS latest_anc_date";

	private static final String WHERE = " WHERE ";

	private static final String SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER = "  SELECT ar.mother_base_entity_id FROM {h-schema}anc_register ar)";

	private static final String HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER = "mi_id_only.mobile_phone_number IS NOT NULL AND mi_id_only.provider_id NOT LIKE '%demo%'";

	private static final String HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER_AND_NO_MOBILE_PHONE_NUMBER_IN_MOTHER_IDENTITY = "me_id_only.mobile_phone_number IS NOT NULL AND me_id_only.provider_id NOT LIKE '%demo%'"
		+ " AND me_id_only.mother_base_entity_id IN (SELECT mi.mother_base_entity_id"
		+ "  FROM {h-schema}mother_identity mi WHERE mi.mobile_phone_number IS NULL)";

	private static final String SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_IDENTITY = ""
		+ "SELECT mi.event_id AS eventId, mi.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = mi.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_identity mi "
		+ "WHERE mi.event_id IN (SELECT MAX(mi_id_only.event_id) OVER (PARTITION BY mi_id_only.mobile_phone_number)"
		+ " FROM {h-schema}mother_identity mi_id_only";

	private static final String SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_EDIT = ""
		+ "SELECT me.event_id AS eventId, me.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = me.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_edit me "
		+ "WHERE me.event_id IN (SELECT MAX(me_id_only.event_id) OVER (PARTITION BY me_id_only.mother_base_entity_id)"
		+ " FROM {h-schema}mother_edit me_id_only";

	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_IDENTITY + " WHERE mi_id_only.event_id > ?1 AND "
		+ HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER;

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_LAST_EDIT_AND_IN_MOTHER_IDENTITY_NO_MOBILE_PHONE_NUMBER = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_EDIT + " WHERE me_id_only.event_id > ?1" + " AND "
		+ HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER_AND_NO_MOBILE_PHONE_NUMBER_IN_MOTHER_IDENTITY
		+ " AND me_id_only.mother_base_entity_id NOT IN (SELECT me_duplicate.mother_base_entity_id"
		+ "  FROM {h-schema}mother_edit me_duplicate WHERE me_duplicate.mobile_phone_number IS NOT NULL"
		+ "  AND me_duplicate.event_id <= ?1 AND me_duplicate.provider_id NOT LIKE '%demo%')";

	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_CURRENT_DATE_MINUS_ANC_VISIT_INTERVAL_IN_DAYS_PLUS_VISIT_REMINDER_INTERVAL_IN_DAYS = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_IDENTITY
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON mi_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER + " AND mi_id_only.mother_base_entity_id IN ("
		+ SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1 + INTERVAL '1 day' * ?2) ORDER BY mi.event_id";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_CURRENT_DATE_MINUS_ANC_VISIT_INTERVAL_IN_DAYS_PLUS_VISIT_REMINDER_INTERVAL_IN_DAYS = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_EDIT
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON me_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER_AND_NO_MOBILE_PHONE_NUMBER_IN_MOTHER_IDENTITY
		+ " AND me_id_only.mother_base_entity_id IN (" + SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1 + INTERVAL '1 day' * ?2) ORDER BY me.event_id";

	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_SOME_DAYS_AGO = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_IDENTITY
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON mi_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER + " AND mi_id_only.mother_base_entity_id IN ("
		+ SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1) ORDER BY mi.event_id";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_SOME_DAYS_AGO = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_EDIT
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON me_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER_AND_NO_MOBILE_PHONE_NUMBER_IN_MOTHER_IDENTITY
		+ " AND me_id_only.mother_base_entity_id IN (" + SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1) ORDER BY me.event_id";
}
