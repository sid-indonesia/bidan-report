package org.sidindonesia.bidanreport.integration.qontak.repository;

import org.sidindonesia.bidanreport.integration.qontak.domain.AutomatedMessageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomatedMessageStatsRepository extends JpaRepository<AutomatedMessageStats, String> {

	@Modifying
	@Query(nativeQuery = true, value = "INSERT " + " INTO "
		+ " {h-schema}automated_message_stats (message_template_id, " + " message_template_name, "
		+ " successful_attempts, " + " failed_attempts, " + " execution_date" + ") " + "VALUES(?1, " + "?2, " + "?3, "
		+ "?4, CURRENT_DATE)  " + "ON " + "CONFLICT (message_template_id, execution_date)  " + "DO " + "UPDATE "
		+ "SET " + " message_template_name = ?2, " + " successful_attempts = ( " + " SELECT " + "  successful_attempts "
		+ " FROM " + "  {h-schema}automated_message_stats " + " WHERE " + "  message_template_id = ?1 "
		+ "AND execution_date = CURRENT_DATE " + " ORDER BY " + "  successful_attempts DESC " + " LIMIT 1) + ?3, "
		+ " failed_attempts = ( " + " SELECT " + "  failed_attempts " + " FROM "
		+ "  {h-schema}automated_message_stats " + " WHERE " + "  message_template_id = ?1 "
		+ "AND execution_date = CURRENT_DATE " + " ORDER BY " + "  failed_attempts DESC " + " LIMIT 1) + ?4")
	int upsert(String messageTemplateId, String messageTemplateName, long successfulAttempts, long failedAttempts);

}
