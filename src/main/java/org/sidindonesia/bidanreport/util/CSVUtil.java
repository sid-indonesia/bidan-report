package org.sidindonesia.bidanreport.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.sidindonesia.bidanreport.repository.projection.HealthEducationProjection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CSVUtil {
	public static final String FULL_NAME = "full_name";
	public static final String PREGNA_TRIMESTER = "pregna_trimester";
	public static final String CALC_GESTATIONAL = "calc_gestational";

	private static final String COMPANY = "Pregnant Women";
	private static final String[] HEADERS = { "phone_number", FULL_NAME, "customer_name", "company", PREGNA_TRIMESTER,
			CALC_GESTATIONAL };

	public static void createContactListCSVFile(List<HealthEducationProjection> healthEducationProjections,
		String csvFileName) throws IOException {

		FileWriter out = new FileWriter(csvFileName);
		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {
			healthEducationProjections.forEach(projection -> {
				try {
					String fullName = projection.getFullName() == null ? "-" : projection.getFullName();
					printer.printRecord(IndonesiaPhoneNumberUtil.sanitize(projection.getMobilePhoneNumber()), fullName,
						fullName, COMPANY, projection.getPregnancyTrimester(),
						projection.getCalculatedGestationalAge());
				} catch (IOException e) {
					log.error("Error writing CSV file: {} with error message: {}, error object: {}", csvFileName,
						e.getMessage(), e);
				}
			});
		}
	}

}
