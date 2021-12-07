package org.sidindonesia.bidanreport.repository.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryConstants {

	private static final String SELECT = " SELECT ";

	private static final String FROM = " FROM ";

	private static final String WHERE = " WHERE ";

	private static final String INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE = "  INNER JOIN (SELECT mother_base_entity_id, MAX(anc_date) AS latest_anc_date";

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

	private static final String PREGNANCY_GAP_COMMA_SEPARATED_VALUES = "CONCAT_WS(',', "
		+ "   CASE WHEN av_sub1.anc_date IS NULL THEN 'N/A' ELSE av_sub1.anc_date\\:\\:varchar END, "
		+ "   CASE WHEN (av_sub1.gestational_age = '') IS NOT FALSE THEN 'N/A' ELSE av_sub1.gestational_age END, "
		+ "   CASE WHEN ((SELECT height_in_cm FROM {h-schema}anc_register WHERE mother_base_entity_id = av_sub1.mother_base_entity_id ORDER BY server_version_epoch DESC LIMIT 1) = '') IS NOT FALSE THEN '-' ELSE (SELECT height_in_cm FROM {h-schema}anc_register WHERE mother_base_entity_id = av_sub1.mother_base_entity_id ORDER BY server_version_epoch DESC LIMIT 1) END, "
		+ "   CASE WHEN (av_sub1.weight_in_kg = '') IS NOT FALSE THEN '-' ELSE av_sub1.weight_in_kg END, "
		+ "   CASE WHEN (av_sub1.mid_upper_arm_circumference_in_cm = '') IS NOT FALSE THEN '-' ELSE av_sub1.mid_upper_arm_circumference_in_cm END, "
		+ "   CASE WHEN (av_sub1.vital_sign_systolic_blood_pressure = '') IS NOT FALSE THEN '-' ELSE av_sub1.vital_sign_systolic_blood_pressure END, "
		+ "   CASE WHEN (av_sub1.vital_sign_diastolic_blood_pressure = '') IS NOT FALSE THEN '-' ELSE av_sub1.vital_sign_diastolic_blood_pressure END, "
		+ "   CASE WHEN (av_sub1.gestational_age\\:\\:integer >= 20) THEN ( "
		+ "    CASE WHEN (av_sub1.uterine_fundal_height = '') IS NOT FALSE THEN '-' ELSE av_sub1.uterine_fundal_height || ' cm' END) ELSE 'Janin belum teraba' END, "
		+ "   CASE WHEN (av_sub1.gestational_age\\:\\:integer >= 20) THEN ( "
		+ "    CASE WHEN (av_sub1.fetal_presentation = '') IS NOT FALSE THEN '-' ELSE av_sub1.fetal_presentation END) ELSE 'Janin belum teraba' END, "
		+ "   CASE WHEN (av_sub1.gestational_age\\:\\:integer >= 12) THEN ( "
		+ "    CASE WHEN (av_sub1.fetal_heart_rate = '') IS NOT FALSE THEN '-' ELSE av_sub1.fetal_heart_rate END) ELSE 'Detak jantung janin belum terdengar' END, "
		+ "   CASE WHEN (av_sub1.tetanus_toxoid_immunization_status = '') IS NOT FALSE THEN '-' ELSE av_sub1.tetanus_toxoid_immunization_status END, "
		+ "   CASE WHEN (av_sub1.is_given_tetanus_toxoid_injection = '') IS NOT FALSE THEN '-' ELSE ( "
		+ "    CASE WHEN av_sub1.is_given_tetanus_toxoid_injection = 'jika_dilakukan' THEN 'Ya' ELSE 'Tidak' END) END,"
		+ "   CASE WHEN (av_sub1.is_given_iron_folic_acid_tablet = '') IS NOT FALSE THEN '-' ELSE av_sub1.is_given_iron_folic_acid_tablet END, "
		+ "   CASE WHEN (EXISTS (SELECT 1 FROM {h-schema}lab_test_anc_visit WHERE mother_base_entity_id = av_sub1.mother_base_entity_id)) THEN ("
		+ "    SELECT" + "     CONCAT_WS(',', "
		+ "      CASE WHEN (ltav.has_proteinuria = '') IS NOT FALSE THEN '-' ELSE ( "
		+ "       CASE WHEN ltav.has_proteinuria = 'Ya' THEN 'positif' ELSE 'negatif' END) END, "
		+ "      CASE WHEN (ltav.hb_level_lab_test_result = '') IS NOT FALSE THEN '-' ELSE ltav.hb_level_lab_test_result END, "
		+ "      CASE WHEN (ltav.is_glucose_blood_more_than_140_mg_dl = '') IS NOT FALSE THEN '-' ELSE ltav.is_glucose_blood_more_than_140_mg_dl END, "
		+ "      CASE WHEN (ltav.has_thalasemia = '') IS NOT FALSE THEN '-' ELSE ltav.has_thalasemia END, "
		+ "      CASE WHEN (ltav.has_syphilis = '') IS NOT FALSE THEN '-' ELSE ltav.has_syphilis END, "
		+ "      CASE WHEN (ltav.has_hbsag = '') IS NOT FALSE THEN '-' ELSE ltav.has_hbsag END, "
		+ "      CASE WHEN (ltav.has_hiv = '') IS NOT FALSE THEN '-' ELSE ltav.has_hiv END "
		+ "     ) FROM {h-schema}lab_test_anc_visit ltav WHERE mother_base_entity_id = av_sub1.mother_base_entity_id ORDER BY server_version_epoch DESC LIMIT 1"
		+ "    ) ELSE '-,-,-,-,-,-,-' END " + "  )";

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
		+ "SELECT " + " mi.event_id AS eventId, " + " mi.mobile_phone_number AS mobilePhoneNumber, " + " ( "
		+ " SELECT " + "  cm.full_name " + " FROM " + "  {h-schema}client_mother cm " + " WHERE "
		+ "  cm.base_entity_id = mi.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + " SELECT " + "  CASE "
		+ "   WHEN (av_sub1.anc_visit_number = '') IS NOT FALSE THEN '0' " + "   ELSE av_sub1.anc_visit_number "
		+ "  END " + " FROM " + "  {h-schema}anc_visit av_sub1 " + " INNER JOIN ( " + "  SELECT "
		+ "   MAX(av_sub2.event_id) AS latest_event_id " + "  FROM " + "   {h-schema}anc_visit av_sub2 "
		+ "  INNER JOIN ( " + "   SELECT " + "    mother_base_entity_id, " + "    MAX(anc_date) AS latest_anc_date "
		+ "   FROM " + "    {h-schema}anc_visit av_sub3 " + "   GROUP BY "
		+ "    mother_base_entity_id) av_max_anc_date ON "
		+ "   (av_sub2.mother_base_entity_id = av_max_anc_date.mother_base_entity_id "
		+ "    AND av_sub2.anc_date = av_max_anc_date.latest_anc_date) " + "  GROUP BY "
		+ "   av_sub2.mother_base_entity_id, " + "   av_sub2.anc_date) av_max_event_id ON "
		+ "  av_sub1.event_id = av_max_event_id.latest_event_id " + " WHERE "
		+ "  av_sub1.mother_base_entity_id = mi.mother_base_entity_id) AS latestAncVisitNumber " + "FROM "
		+ " {h-schema}mother_identity mi " + "WHERE " + " mi.event_id IN ( " + " SELECT "
		+ "  MAX(mi_id_only.event_id) OVER (PARTITION BY mi_id_only.mobile_phone_number) " + " FROM "
		+ "  {h-schema}mother_identity mi_id_only " + " INNER JOIN {h-schema}anc_register ar ON "
		+ "  mi_id_only.mother_base_entity_id = ar.mother_base_entity_id " + " INNER JOIN ( " + "  SELECT "
		+ "   MAX(ar_sub.event_id) AS latest_event_id " + "  FROM " + "   {h-schema}anc_register ar_sub "
		+ "  GROUP BY " + "   ar_sub.mother_base_entity_id) ar_max_event_id ON "
		+ "  ar.event_id = ar_max_event_id.latest_event_id " + " INNER JOIN ( " + "  SELECT "
		+ "   mother_base_entity_id, " + "   MAX(anc_date) AS latest_anc_date " + "  FROM " + "   {h-schema}anc_visit "
		+ "  GROUP BY " + "   1) av ON " + "  mi_id_only.mother_base_entity_id = av.mother_base_entity_id " + " WHERE "
		+ "  mi_id_only.mobile_phone_number IS NOT NULL " + "  AND mi_id_only.provider_id NOT LIKE '%demo%' "
		+ "  AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1 + INTERVAL '1 day' * ?2 " + "  AND "
		+ "  (CASE " + "   WHEN (EXISTS ( " + "   SELECT " + "    1 " + "   FROM " + "    {h-schema}anc_close ac "
		+ "   WHERE " + "    ac.mother_base_entity_id = ar.mother_base_entity_id)) THEN ( " + "   NOT (( "
		+ "   SELECT " + "    ac.date_created " + "   FROM " + "    {h-schema}anc_close ac " + "   WHERE "
		+ "    ac.mother_base_entity_id = ar.mother_base_entity_id " + "   ORDER BY " + "    ac.event_id DESC, "
		+ "    ac.server_version_epoch DESC "
		+ "   LIMIT 1) BETWEEN ar.last_menstrual_period_date AND ar.expected_delivery_date)) " + "   ELSE TRUE "
		+ "  END)) " + "ORDER BY " + " mi.event_id";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_CURRENT_DATE_MINUS_ANC_VISIT_INTERVAL_IN_DAYS_PLUS_VISIT_REMINDER_INTERVAL_IN_DAYS = ""
		+ "SELECT " + " me.event_id AS eventId, " + " me.mobile_phone_number AS mobilePhoneNumber, " + " ( "
		+ " SELECT " + "  cm.full_name " + " FROM " + "  {h-schema}client_mother cm " + " WHERE "
		+ "  cm.base_entity_id = me.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + " SELECT " + "  CASE "
		+ "   WHEN (av_sub1.anc_visit_number = '') IS NOT FALSE THEN '0' " + "   ELSE av_sub1.anc_visit_number "
		+ "  END " + " FROM " + "  {h-schema}anc_visit av_sub1 " + " INNER JOIN ( " + "  SELECT "
		+ "   MAX(av_sub2.event_id) AS latest_event_id " + "  FROM " + "   {h-schema}anc_visit av_sub2 "
		+ "  INNER JOIN ( " + "   SELECT " + "    mother_base_entity_id, " + "    MAX(anc_date) AS latest_anc_date "
		+ "   FROM " + "    {h-schema}anc_visit av_sub3 " + "   GROUP BY "
		+ "    mother_base_entity_id) av_max_anc_date ON "
		+ "   (av_sub2.mother_base_entity_id = av_max_anc_date.mother_base_entity_id "
		+ "    AND av_sub2.anc_date = av_max_anc_date.latest_anc_date) " + "  GROUP BY "
		+ "   av_sub2.mother_base_entity_id, " + "   av_sub2.anc_date) av_max_event_id ON "
		+ "  av_sub1.event_id = av_max_event_id.latest_event_id " + " WHERE "
		+ "  av_sub1.mother_base_entity_id = me.mother_base_entity_id) AS latestAncVisitNumber " + "FROM "
		+ " {h-schema}mother_edit me " + "WHERE " + " me.event_id IN ( " + " SELECT "
		+ "  MAX(me_id_only.event_id) OVER (PARTITION BY me_id_only.mother_base_entity_id) " + " FROM "
		+ "  {h-schema}mother_edit me_id_only " + " INNER JOIN {h-schema}anc_register ar ON "
		+ "  me_id_only.mother_base_entity_id = ar.mother_base_entity_id " + " INNER JOIN ( " + "  SELECT "
		+ "   MAX(ar_sub.event_id) AS latest_event_id " + "  FROM " + "   {h-schema}anc_register ar_sub "
		+ "  GROUP BY " + "   ar_sub.mother_base_entity_id) ar_max_event_id ON "
		+ "  ar.event_id = ar_max_event_id.latest_event_id " + " INNER JOIN ( " + "  SELECT "
		+ "   mother_base_entity_id, " + "   MAX(anc_date) AS latest_anc_date " + "  FROM " + "   {h-schema}anc_visit "
		+ "  GROUP BY " + "   1) av ON " + "  me_id_only.mother_base_entity_id = av.mother_base_entity_id " + " WHERE "
		+ "  me_id_only.mobile_phone_number IS NOT NULL " + "  AND me_id_only.provider_id NOT LIKE '%demo%' "
		+ "  AND me_id_only.mother_base_entity_id IN ( " + "  SELECT " + "   mi.mother_base_entity_id " + "  FROM "
		+ "   {h-schema}mother_identity mi " + "  WHERE " + "   mi.mobile_phone_number IS NULL) "
		+ "  AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1 + INTERVAL '1 day' * ?2 " + "  AND "
		+ "  (CASE " + "   WHEN (EXISTS ( " + "   SELECT " + "    1 " + "   FROM " + "    {h-schema}anc_close ac "
		+ "   WHERE " + "    ac.mother_base_entity_id = ar.mother_base_entity_id)) THEN ( " + "   NOT (( "
		+ "   SELECT " + "    ac.date_created " + "   FROM " + "    {h-schema}anc_close ac " + "   WHERE "
		+ "    ac.mother_base_entity_id = ar.mother_base_entity_id " + "   ORDER BY " + "    ac.event_id DESC, "
		+ "    ac.server_version_epoch DESC "
		+ "   LIMIT 1) BETWEEN ar.last_menstrual_period_date AND ar.expected_delivery_date)) " + "   ELSE TRUE "
		+ "  END)) " + "ORDER BY " + " me.event_id";

	// unused
	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_SOME_DAYS_AGO = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_IDENTITY
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON mi_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER + " AND mi_id_only.mother_base_entity_id IN ("
		+ SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1) ORDER BY mi.event_id";

	// unused
	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_DATE_IS_SOME_DAYS_AGO = ""
		+ SELECT_EVENT_ID_AND_MOBILE_PHONE_NUMBER_FROM_MOTHER_EDIT
		+ INNER_JOIN_SELECT_MOTHER_BASE_ENTITY_ID_MAX_ANC_DATE_AS_LATEST_ANC_DATE
		+ "  FROM {h-schema}anc_visit GROUP BY 1) av ON me_id_only.mother_base_entity_id = av.mother_base_entity_id"
		+ WHERE + HAS_MOBILE_PHONE_NUMBER_AND_NOT_DEMO_USER_AND_NO_MOBILE_PHONE_NUMBER_IN_MOTHER_IDENTITY
		+ " AND me_id_only.mother_base_entity_id IN (" + SELECT_MOTHER_BASE_ENTITY_ID_FROM_ANC_REGISTER
		+ " AND av.latest_anc_date = current_date - INTERVAL '1 day' * ?1) ORDER BY me.event_id";

	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_PREGNANCY_GAP = ""
		+ "SELECT " + " mi.event_id AS eventId, " + " mi.mobile_phone_number AS mobilePhoneNumber, " + " ( " + SELECT
		+ "  cm.full_name " + FROM + "  {h-schema}client_mother cm " + WHERE
		+ "  cm.base_entity_id = mi.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + SELECT + PREGNANCY_GAP_COMMA_SEPARATED_VALUES + FROM
		+ "  {h-schema}anc_visit av_sub1 " + " INNER JOIN ( " + SELECT + "   MAX(av_sub2.event_id) AS latest_event_id "
		+ FROM + "   {h-schema}anc_visit av_sub2 " + "  INNER JOIN ( " + "   SELECT " + "    mother_base_entity_id, "
		+ "    MAX(anc_date) AS latest_anc_date " + "   FROM " + "    {h-schema}anc_visit av_sub3 " + "   GROUP BY "
		+ "    mother_base_entity_id) av_max_anc_date ON "
		+ "   (av_sub2.mother_base_entity_id = av_max_anc_date.mother_base_entity_id "
		+ "    AND av_sub2.anc_date = av_max_anc_date.latest_anc_date) " + "  GROUP BY "
		+ "   av_sub2.mother_base_entity_id, " + "   av_sub2.anc_date) av_max_event_id ON "
		+ "  av_sub1.event_id = av_max_event_id.latest_event_id " + WHERE
		+ "  av_sub1.mother_base_entity_id = mi.mother_base_entity_id "
		+ "  AND av_sub1.event_id > ?1) AS pregnancyGapCommaSeparatedValues " + "FROM "
		+ " {h-schema}mother_identity mi " + "WHERE " + " mi.event_id IN ( " + SELECT + "  MAX(mi_id_only.event_id) "
		+ FROM + "  {h-schema}mother_identity mi_id_only " + " INNER JOIN {h-schema}anc_visit av ON "
		+ "  mi_id_only.mother_base_entity_id = av.mother_base_entity_id " + WHERE
		+ "  mi_id_only.mobile_phone_number IS NOT NULL " + "  AND mi_id_only.provider_id NOT LIKE '%demo%' "
		+ "  AND mi_id_only.mother_base_entity_id IN ( " + SELECT + "   ar.mother_base_entity_id " + FROM
		+ "   {h-schema}anc_register ar) " + "  AND av.event_id > ?1 " + " GROUP BY "
		+ "  mi_id_only.mobile_phone_number) " + "ORDER BY " + " mi.event_id";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LATEST_ANC_VISIT_PREGNANCY_GAP = "" + "SELECT "
		+ " me.event_id AS eventId, " + " me.mobile_phone_number AS mobilePhoneNumber, " + " ( " + SELECT
		+ "  cm.full_name " + FROM + "  {h-schema}client_mother cm " + WHERE
		+ "  cm.base_entity_id = me.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + SELECT + PREGNANCY_GAP_COMMA_SEPARATED_VALUES + FROM
		+ "  {h-schema}anc_visit av_sub1 " + " INNER JOIN ( " + SELECT + "   MAX(av_sub2.event_id) AS latest_event_id "
		+ FROM + "   {h-schema}anc_visit av_sub2 " + "  INNER JOIN ( " + "   SELECT " + "    mother_base_entity_id, "
		+ "    MAX(anc_date) AS latest_anc_date " + "   FROM " + "    {h-schema}anc_visit av_sub3 " + "   GROUP BY "
		+ "    mother_base_entity_id) av_max_anc_date ON "
		+ "   (av_sub2.mother_base_entity_id = av_max_anc_date.mother_base_entity_id "
		+ "    AND av_sub2.anc_date = av_max_anc_date.latest_anc_date) " + "  GROUP BY "
		+ "   av_sub2.mother_base_entity_id, " + "   av_sub2.anc_date) av_max_event_id ON "
		+ "  av_sub1.event_id = av_max_event_id.latest_event_id " + WHERE
		+ "  av_sub1.mother_base_entity_id = me.mother_base_entity_id "
		+ "  AND av_sub1.event_id > ?1) AS pregnancyGapCommaSeparatedValues " + "FROM " + " {h-schema}mother_edit me "
		+ "WHERE " + " me.event_id IN ( " + SELECT + "  MAX(me_id_only.event_id) " + FROM
		+ "  {h-schema}mother_edit me_id_only " + " INNER JOIN {h-schema}anc_visit av ON "
		+ "  me_id_only.mother_base_entity_id = av.mother_base_entity_id " + WHERE
		+ "  me_id_only.mobile_phone_number IS NOT NULL " + "  AND me_id_only.provider_id NOT LIKE '%demo%' "
		+ "  AND me_id_only.mother_base_entity_id IN ( " + SELECT + "   mi.mother_base_entity_id " + FROM
		+ "   {h-schema}mother_identity mi " + "  WHERE " + "   mi.mobile_phone_number IS NULL) "
		+ "  AND me_id_only.mother_base_entity_id IN ( " + SELECT + "   ar.mother_base_entity_id " + FROM
		+ "   {h-schema}anc_register ar) " + "  AND av.event_id > ?1 " + " GROUP BY "
		+ "  me_id_only.mobile_phone_number) " + "ORDER BY " + " me.event_id";

	public static final String MOTHER_IDENTITY_NATIVE_QUERY_FIND_ALL_WITH_LAST_MENSTRUAL_PERIOD_DATE_NO_EARLIER_THAN_42_WEEKS_AGO_OR_EXPECTED_DELIVERY_DATE_IS_STILL_IN_THE_FUTURE = ""
		+ "SELECT " + " mi.event_id AS eventId, " + " mi.mobile_phone_number AS mobilePhoneNumber, " + " ( "
		+ " SELECT " + "  cm.full_name " + " FROM " + "  {h-schema}client_mother cm " + " WHERE "
		+ "  cm.base_entity_id = mi.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + " SELECT " + "  CASE "
		+ "   WHEN calculated.gestational_age >= 29 THEN '3' " + "   ELSE " + "   (CASE "
		+ "    WHEN (calculated.gestational_age >= 13 " + "     AND calculated.gestational_age <= 28) THEN '2' "
		+ "    ELSE " + "    (CASE " + "     WHEN (calculated.gestational_age >= 0 "
		+ "      AND calculated.gestational_age <= 12) THEN '1' " + "     ELSE '-' " + "    END) " + "   END) "
		+ "  END " + " FROM " + "  ( " + "  SELECT "
		+ "   ((CURRENT_DATE - ar_sub.last_menstrual_period_date) / 7) AS gestational_age " + "  FROM "
		+ "   {h-schema}anc_register ar_sub " + "  WHERE "
		+ "   mi.mother_base_entity_id = ar_sub.mother_base_entity_id " + "  ORDER BY " + "   ar_sub.event_id DESC, "
		+ "   ar_sub.last_menstrual_period_date DESC " + "  LIMIT 1) calculated) AS pregnancyTrimester, " + " ( "
		+ " SELECT " + "  ((CURRENT_DATE - ar_sub1.last_menstrual_period_date) / 7) " + " FROM "
		+ "  {h-schema}anc_register ar_sub1 " + " WHERE "
		+ "  mi.mother_base_entity_id = ar_sub1.mother_base_entity_id " + " ORDER BY " + "  ar_sub1.event_id DESC, "
		+ "  ar_sub1.last_menstrual_period_date DESC " + " LIMIT 1) AS calculatedGestationalAge " + "FROM "
		+ " {h-schema}mother_identity mi " + "WHERE " + " mi.event_id IN ( " + " SELECT "
		+ "  MAX(mi_id_only.event_id) " + " FROM " + "  {h-schema}mother_identity mi_id_only "
		+ " INNER JOIN {h-schema}anc_register ar ON " + "  mi_id_only.mother_base_entity_id = ar.mother_base_entity_id "
		+ " INNER JOIN ( " + "  SELECT " + "   MAX(ar_sub.event_id) AS latest_event_id " + "  FROM "
		+ "   {h-schema}anc_register ar_sub " + "  GROUP BY " + "   ar_sub.mother_base_entity_id) ar_max_event_id ON "
		+ "  ar.event_id = ar_max_event_id.latest_event_id " + " WHERE "
		+ "  mi_id_only.mobile_phone_number IS NOT NULL " + "  AND mi_id_only.provider_id NOT LIKE '%demo%' " + "  AND "
		+ "  (CASE " + "   WHEN (EXISTS ( " + "   SELECT " + "    1 " + "   FROM " + "    {h-schema}anc_close ac "
		+ "   WHERE " + "    ac.mother_base_entity_id = ar.mother_base_entity_id)) THEN ( " + "    NOT (( "
		+ "   SELECT " + "    ac.date_created " + "   FROM " + "    {h-schema}anc_close ac " + "   WHERE "
		+ "    ac.mother_base_entity_id = ar.mother_base_entity_id " + "   ORDER BY " + "    ac.event_id DESC, "
		+ "    ac.server_version_epoch DESC "
		+ "   LIMIT 1) BETWEEN ar.last_menstrual_period_date AND ar.expected_delivery_date)) " + "   ELSE TRUE "
		+ "  END) " + "  AND ((ar.last_menstrual_period_date >= (CURRENT_DATE - INTERVAL '42 weeks')) "
		+ "   OR (ar.expected_delivery_date > CURRENT_DATE)) " + " GROUP BY " + "  mi_id_only.mobile_phone_number) "
		+ "ORDER BY " + " mi.event_id";

	public static final String MOTHER_EDIT_NATIVE_QUERY_FIND_ALL_WITH_LAST_MENSTRUAL_PERIOD_DATE_NO_EARLIER_THAN_42_WEEKS_AGO_OR_EXPECTED_DELIVERY_DATE_IS_STILL_IN_THE_FUTURE = ""
		+ "SELECT " + " me.event_id AS eventId, " + " me.mobile_phone_number AS mobilePhoneNumber, " + " ( "
		+ " SELECT " + "  cm.full_name " + " FROM " + "  {h-schema}client_mother cm " + " WHERE "
		+ "  cm.base_entity_id = me.mother_base_entity_id " + " ORDER BY " + "  cm.server_version_epoch DESC "
		+ " LIMIT 1) AS fullName, " + " ( " + " SELECT " + "  CASE "
		+ "   WHEN calculated.gestational_age >= 29 THEN '3' " + "   ELSE " + "   (CASE "
		+ "    WHEN (calculated.gestational_age >= 13 " + "     AND calculated.gestational_age <= 28) THEN '2' "
		+ "    ELSE " + "    (CASE " + "     WHEN (calculated.gestational_age >= 0 "
		+ "      AND calculated.gestational_age <= 12) THEN '1' " + "     ELSE '-' " + "    END) " + "   END) "
		+ "  END " + " FROM " + "  ( " + "  SELECT "
		+ "   ((CURRENT_DATE - ar_sub.last_menstrual_period_date) / 7) AS gestational_age " + "  FROM "
		+ "   {h-schema}anc_register ar_sub " + "  WHERE "
		+ "   me.mother_base_entity_id = ar_sub.mother_base_entity_id " + "  ORDER BY " + "   ar_sub.event_id DESC, "
		+ "   ar_sub.last_menstrual_period_date DESC " + "  LIMIT 1) calculated) AS pregnancyTrimester, " + " ( "
		+ " SELECT " + "  ((CURRENT_DATE - ar_sub1.last_menstrual_period_date) / 7) " + " FROM "
		+ "  {h-schema}anc_register ar_sub1 " + " WHERE "
		+ "  me.mother_base_entity_id = ar_sub1.mother_base_entity_id " + " ORDER BY " + "  ar_sub1.event_id DESC, "
		+ "  ar_sub1.last_menstrual_period_date DESC " + " LIMIT 1) AS calculatedGestationalAge " + "FROM "
		+ " {h-schema}mother_edit me " + "WHERE " + " me.event_id IN ( " + " SELECT " + "  MAX(me_id_only.event_id) "
		+ " FROM " + "  {h-schema}mother_edit me_id_only " + " INNER JOIN {h-schema}anc_register ar ON "
		+ "  me_id_only.mother_base_entity_id = ar.mother_base_entity_id " + " INNER JOIN ( " + "  SELECT "
		+ "   MAX(ar_sub.event_id) AS latest_event_id " + "  FROM " + "   {h-schema}anc_register ar_sub "
		+ "  GROUP BY " + "   ar_sub.mother_base_entity_id) ar_max_event_id ON "
		+ "  ar.event_id = ar_max_event_id.latest_event_id " + " WHERE "
		+ "  me_id_only.mobile_phone_number IS NOT NULL " + "  AND me_id_only.provider_id NOT LIKE '%demo%' "
		+ "  AND me_id_only.mother_base_entity_id IN ( " + "  SELECT " + "   mi.mother_base_entity_id " + "  FROM "
		+ "   {h-schema}mother_identity mi " + "  WHERE " + "   mi.mobile_phone_number IS NULL) " + "  AND "
		+ "  (CASE " + "   WHEN (EXISTS ( " + "   SELECT " + "    1 " + "   FROM " + "    {h-schema}anc_close ac "
		+ "   WHERE " + "    ac.mother_base_entity_id = ar.mother_base_entity_id)) THEN ( " + "    NOT (( "
		+ "   SELECT " + "    ac.date_created " + "   FROM " + "    {h-schema}anc_close ac " + "   WHERE "
		+ "    ac.mother_base_entity_id = ar.mother_base_entity_id " + "   ORDER BY " + "    ac.event_id DESC, "
		+ "    ac.server_version_epoch DESC "
		+ "   LIMIT 1) BETWEEN ar.last_menstrual_period_date AND ar.expected_delivery_date)) " + "   ELSE TRUE "
		+ "  END) " + "  AND ((ar.last_menstrual_period_date >= (CURRENT_DATE - INTERVAL '42 weeks')) "
		+ "   OR (ar.expected_delivery_date > CURRENT_DATE)) " + " GROUP BY " + "  me_id_only.mobile_phone_number) "
		+ "ORDER BY " + " me.event_id";
}
