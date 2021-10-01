package org.sidindonesia.bidanreport.repository.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryConstants {
	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_NEW_ONES = ""
		+ "SELECT mi.event_id AS eventId, mi.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = mi.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_identity mi "
		+ "WHERE mi.event_id IN (SELECT MAX(mi_id_only.event_id) OVER (PARTITION BY mi_id_only.mobile_phone_number)"
		+ " FROM {h-schema}mother_identity mi_id_only"
		+ " WHERE mi_id_only.event_id > ?1 AND mi_id_only.mobile_phone_number IS NOT NULL AND mi_id_only.provider_id NOT LIKE '%demo%'";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_LAST_EDIT_AND_IN_MOTHER_IDENTITY_NO_MOBILE_PHONE_NUMBER = ""
		+ "SELECT me.event_id AS eventId, me.mobile_phone_number AS mobilePhoneNumber, "
		+ "(SELECT cm.full_name FROM {h-schema}client_mother cm "
		+ "WHERE cm.base_entity_id = me.mother_base_entity_id ORDER BY cm.server_version_epoch DESC LIMIT 1) AS fullName "
		+ "FROM {h-schema}mother_edit me "
		+ "WHERE me.event_id IN (SELECT MAX(me_id_only.event_id) OVER (PARTITION BY me_id_only.mother_base_entity_id)"
		+ " FROM {h-schema}mother_edit me_id_only WHERE me_id_only.event_id > ?1"
		+ " AND me_id_only.mobile_phone_number IS NOT NULL AND me_id_only.provider_id NOT LIKE '%demo%'"
		+ " AND me_id_only.mother_base_entity_id IN (SELECT mi.mother_base_entity_id"
		+ "  FROM {h-schema}mother_identity mi WHERE mi.mobile_phone_number IS NULL)"
		+ " AND me_id_only.mother_base_entity_id NOT IN (SELECT me_duplicate.mother_base_entity_id"
		+ "  FROM {h-schema}mother_edit me_duplicate WHERE me_duplicate.mobile_phone_number IS NOT NULL"
		+ "  AND me_duplicate.event_id <= ?1 AND me_duplicate.provider_id NOT LIKE '%demo%')";
}
