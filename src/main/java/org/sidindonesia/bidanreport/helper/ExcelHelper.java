package org.sidindonesia.bidanreport.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.sidindonesia.bidanreport.exception.ExcelWriteException;
import org.sidindonesia.bidanreport.util.CamelCaseUtil;
import org.sidindonesia.bidanreport.util.ReflectionsUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelHelper {
	public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public static ByteArrayInputStream allEntitiesToExcelSheets() {

		try (Workbook workbook = new SXSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			Set<Class<?>> entityClasses = ReflectionsUtil.getAllEntityClasses();

			entityClasses.stream().forEach(entityClass -> {
				Sheet sheet = workbook.createSheet(CamelCaseUtil.camelToSnake(entityClass.getSimpleName()));

				Row headerRow = sheet.createRow(0);

				Field[] fields = entityClass.getDeclaredFields();
				int headerCol = 0;
				for (Field field : fields) {
					Cell cell = headerRow.createCell(headerCol++);
					cell.setCellValue(CamelCaseUtil.camelToSnake(field.getName()));
				}

			});
//
//			int rowIdx = 1;
//			for (AncClose ancClose : ancCloses) {
//				Row row = sheet.createRow(rowIdx++);
//
//				row.createCell(0).setCellValue(ancClose.getEventId());
//				row.createCell(1).setCellValue(ancClose.getBirthAttendant());
//			}
//
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new ExcelWriteException("Fail to import data to Excel file: " + e.getMessage());
		}
	}
}