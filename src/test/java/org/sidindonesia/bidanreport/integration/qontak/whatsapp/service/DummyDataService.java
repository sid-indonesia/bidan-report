package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service;

import java.time.LocalDate;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class DummyDataService {
	@Autowired
	private JdbcOperations jdbcOperations;
	@Autowired
	private QontakProperties qontakProperties;

	void insertDummyData() {
		IntStream.rangeClosed(1, 2).forEach(insertIntoClientMother());
		IntStream.rangeClosed(1, 3).forEach(insertIntoMotherIdentity());
		IntStream.rangeClosed(2, 3).forEach(insertIntoAncRegister());

		IntStream.rangeClosed(4, 5).forEach(insertIntoClientMother());
		IntStream.rangeClosed(4, 6).forEach(insertIntoMotherIdentityWithoutMobilePhoneNumber());
		IntStream.rangeClosed(5, 6).forEach(insertIntoAncRegister());
		IntStream.rangeClosed(7, 9).forEach(insertIntoMotherEdit());
	}

	private IntConsumer insertIntoClientMother() {
		return id -> {
			jdbcOperations.execute("INSERT INTO client_mother\n"
				+ "(source_id, date_created, base_entity_id, birth_date, server_version_epoch, source_date_deleted, full_name)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, '" + id + "', CURRENT_TIMESTAMP, '157663657225" + id
				+ "', CURRENT_TIMESTAMP, 'Test " + id + "');\n");
		};
	}

	private IntConsumer insertIntoMotherIdentity() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_identity\n"
				+ "(event_id, date_created, event_date, mobile_phone_number, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '08381234567" + id + "', '" + id
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	private IntConsumer insertIntoAncRegister() {
		return id -> {
			jdbcOperations.execute("INSERT INTO anc_register\n"
				+ "(event_id, date_created, mother_base_entity_id, provider_id, server_version_epoch)\n" + "VALUES("
				+ id + ", CURRENT_TIMESTAMP, '" + id + "', 'test', '157663657225" + id + "');\n");
		};
	}

	private IntConsumer insertIntoMotherIdentityWithoutMobilePhoneNumber() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_identity\n"
				+ "(event_id, date_created, event_date, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '" + id
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	private IntConsumer insertIntoMotherEdit() {
		return id -> {
			jdbcOperations.execute("INSERT INTO mother_edit\n"
				+ "(event_id, date_created, event_date, mobile_phone_number, mother_base_entity_id, provider_id, registration_date, server_version_epoch, source_date_deleted, transfer_date)\n"
				+ "VALUES(" + id + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '08381234567" + id + "', '" + (id - 3)
				+ "', 'test', CURRENT_TIMESTAMP, '157663657225" + id + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);\n");
		};
	}

	public IntConsumer insertIntoAncVisitForVisitReminder() {
		return id -> {
			jdbcOperations.execute("INSERT INTO anc_visit (event_id, mother_base_entity_id, anc_date)\n" + "VALUES("
				+ id + ", '" + id + "', '" + LocalDate.now().minusMonths(1)
					.plusDays(qontakProperties.getWhatsApp().getVisitReminderIntervalInDays())
				+ "');\n");
		};
	}

	public IntConsumer insertIntoAncVisitForPregnancyGap() {
		return id -> {
			jdbcOperations.execute("INSERT INTO anc_visit (event_id, mother_base_entity_id, anc_date)\n" + "VALUES("
				+ id + ", '" + id + "', '"
				+ LocalDate.now().minusDays(qontakProperties.getWhatsApp().getPregnancyGapIntervalInDays()) + "');\n");
		};
	}
}
