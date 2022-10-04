package org.sidindonesia.bidanreport.util;

import static java.util.stream.Collectors.toList;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.sidindonesia.bidanreport.repository.projection.AncVisitReminderProjection;
import org.sidindonesia.bidanreport.repository.projection.HealthEducationProjection;
import org.sidindonesia.bidanreport.repository.projection.MotherIdentityWhatsAppProjection;
import org.sidindonesia.bidanreport.repository.projection.PregnancyGapProjection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CSVUtil {
	private static final String CUSTOMER_NAME = "customer_name";
	private static final String PHONE_NUMBER = "phone_number";
	private static final String COMPANY_KEY = "company";

	public static final String FULL_NAME = "full_name";
	public static final String PREGNA_TRIMESTER = "pregna_trimester";
	public static final String CALC_GESTATIONAL = "calc_gestational";

	public static final String DHO = "dho";

	public static final String ANC_DATE = "anc_date";
	public static final String GESTATIONAL_AGE = "gestational_age";
	public static final String HEIGHT_IN_CM = "height_in_cm";
	public static final String WEIGHT_IN_KG = "weight_in_kg";
	public static final String MUAC_IN_CM = "muac_in_cm";
	public static final String SYSTOLIC_BP = "systolic_bp";
	public static final String DIASTOLIC_BP = "diastolic_bp";
	public static final String UTERINE_F_HEIGHT = "uterine_f_height";
	public static final String FETAL_PRESENTATI = "fetal_presentati";
	public static final String FETAL_HEART_RATE = "fetal_heart_rate";
	public static final String TETANUS_T_IMM_ST = "tetanus_t_imm_st";
	public static final String GIVEN_TT_INJECTI = "given_tt_injecti";
	public static final String GIVEN_IFA_TABLET = "given_ifa_tablet";
	public static final String HAS_PROTEINURIA = "has_proteinuria";
	public static final String HB_LEVEL_RESULT = "hb_level_result";
	public static final String GLUCOSE_140_MGDL = "glucose_140_mgdl";
	public static final String HAS_THALASEMIA = "has_thalasemia";
	public static final String HAS_SYPHILIS = "has_syphilis";
	public static final String HAS_HBSAG = "has_hbsag";
	public static final String HAS_HIV = "has_hiv";

	public static final String VISIT_NUMBER = "visit_number";

	private static final String COMPANY_VALUE = "Pregnant Women";
	private static final String[] HEADERS_FOR_HEALTH_EDUCATION = { PHONE_NUMBER, FULL_NAME, CUSTOMER_NAME, COMPANY_KEY,
			PREGNA_TRIMESTER, CALC_GESTATIONAL };
	private static final String[] HEADERS_FOR_INTRO = { PHONE_NUMBER, FULL_NAME, CUSTOMER_NAME, COMPANY_KEY, DHO };
	private static final String[] HEADERS_FOR_PREGNANCY_GAP = { PHONE_NUMBER, FULL_NAME, CUSTOMER_NAME, COMPANY_KEY,
			ANC_DATE, GESTATIONAL_AGE, HEIGHT_IN_CM, WEIGHT_IN_KG, MUAC_IN_CM, SYSTOLIC_BP, DIASTOLIC_BP,
			UTERINE_F_HEIGHT, FETAL_PRESENTATI, FETAL_HEART_RATE, TETANUS_T_IMM_ST, GIVEN_TT_INJECTI, GIVEN_IFA_TABLET,
			HAS_PROTEINURIA, HB_LEVEL_RESULT, GLUCOSE_140_MGDL, HAS_THALASEMIA, HAS_SYPHILIS, HAS_HBSAG, HAS_HIV };
	private static final String[] HEADERS_FOR_ANC_VISIT_REMINDER = { PHONE_NUMBER, FULL_NAME, CUSTOMER_NAME,
			COMPANY_KEY, VISIT_NUMBER };

	private static final String ERROR_WRITING_CSV_FILE_WITH_ERROR_MESSAGE_ERROR_OBJECT = "Error writing CSV file: {} with error message: {}, error object: {}";

	public static void createContactListCSVFileForHealthEducation(
		List<HealthEducationProjection> healthEducationProjections, String csvFileName) throws IOException {
		FileWriter out = new FileWriter(csvFileName);
		try (CSVPrinter printer = new CSVPrinter(out,
			CSVFormat.DEFAULT.builder().setHeader(HEADERS_FOR_HEALTH_EDUCATION).build())) {
			healthEducationProjections.forEach(projection -> {
				try {
					String fullName = projection.getFullName() == null ? "-" : projection.getFullName();
					printer.printRecord(IndonesiaPhoneNumberUtil.sanitize(projection.getMobilePhoneNumber()), fullName,
						fullName, COMPANY_VALUE, projection.getPregnancyTrimester(),
						projection.getCalculatedGestationalAge());
				} catch (IOException e) {
					log.error(ERROR_WRITING_CSV_FILE_WITH_ERROR_MESSAGE_ERROR_OBJECT, csvFileName, e.getMessage(), e);
				}
			});
		}
	}

	public static void createContactListCSVFileForIntroMessage(
		List<MotherIdentityWhatsAppProjection> motherIdentityProjections, String csvFileName,
		String districtHealthOffice) throws IOException {
		FileWriter out = new FileWriter(csvFileName);
		try (CSVPrinter printer = new CSVPrinter(out,
			CSVFormat.DEFAULT.builder().setHeader(HEADERS_FOR_INTRO).build())) {
			motherIdentityProjections.forEach(projection -> {
				try {
					String fullName = projection.getFullName() == null ? "-" : projection.getFullName();
					printer.printRecord(IndonesiaPhoneNumberUtil.sanitize(projection.getMobilePhoneNumber()), fullName,
						fullName, COMPANY_VALUE, districtHealthOffice);
				} catch (IOException e) {
					log.error(ERROR_WRITING_CSV_FILE_WITH_ERROR_MESSAGE_ERROR_OBJECT, csvFileName, e.getMessage(), e);
				}
			});
		}
	}

	public static void createContactListCSVFileForPregnancyGapMessage(
		List<PregnancyGapProjection> pregnancyGapProjections, String csvFileName) throws IOException {
		FileWriter out = new FileWriter(csvFileName);
		try (CSVPrinter printer = new CSVPrinter(out,
			CSVFormat.DEFAULT.builder().setHeader(HEADERS_FOR_PREGNANCY_GAP).build())) {
			pregnancyGapProjections.forEach(projection -> {
				try {
					String fullName = projection.getFullName() == null ? "-" : projection.getFullName();
					List<String> values = Stream
						.of(IndonesiaPhoneNumberUtil.sanitize(projection.getMobilePhoneNumber()), fullName, fullName,
							COMPANY_VALUE)
						.collect(toList());
					String csv = projection.getPregnancyGapCommaSeparatedValues();
					List<String> pregnancyGapValues = Stream.of(csv.split(",")).map(String::trim).collect(toList());
					values.addAll(pregnancyGapValues);

					printer.printRecord(values);
				} catch (IOException e) {
					log.error(ERROR_WRITING_CSV_FILE_WITH_ERROR_MESSAGE_ERROR_OBJECT, csvFileName, e.getMessage(), e);
				}
			});
		}
	}

	public static void createContactListCSVFileForANCVisitReminderMessage(
		List<AncVisitReminderProjection> allPregnantWomenToBeRemindedForTheNextANCVisit, String csvFileName)
		throws IOException {
		FileWriter out = new FileWriter(csvFileName);
		try (CSVPrinter printer = new CSVPrinter(out,
			CSVFormat.DEFAULT.builder().setHeader(HEADERS_FOR_ANC_VISIT_REMINDER).build())) {
			allPregnantWomenToBeRemindedForTheNextANCVisit.forEach(projection -> {
				try {
					String fullName = projection.getFullName() == null ? "-" : projection.getFullName();
					List<String> values = Stream
						.of(IndonesiaPhoneNumberUtil.sanitize(projection.getMobilePhoneNumber()), fullName, fullName,
							COMPANY_VALUE, String.valueOf(projection.getLatestAncVisitNumber() + 1))
						.collect(toList());

					printer.printRecord(values);
				} catch (IOException e) {
					log.error(ERROR_WRITING_CSV_FILE_WITH_ERROR_MESSAGE_ERROR_OBJECT, csvFileName, e.getMessage(), e);
				}
			});
		}
	}
}
