package org.sidindonesia.bidanreport.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.sidindonesia.bidanreport.domain.AncClose;
import org.sidindonesia.bidanreport.exception.ExcelWriteException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelHelper {
	public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	static final String[] HEADERS = { "Event ID", "Birth Attendant" };
	static final String SHEET = "AncCloses";

	public static ByteArrayInputStream ancClosesToExcel(List<AncClose> ancCloses) {

		try (Workbook workbook = new SXSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERS[col]);
			}

			int rowIdx = 1;
			for (AncClose ancClose : ancCloses) {
				Row row = sheet.createRow(rowIdx++);

				row.createCell(0).setCellValue(ancClose.getEventId());
				row.createCell(1).setCellValue(ancClose.getBirthAttendant());
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new ExcelWriteException("Fail to import data to Excel file: " + e.getMessage());
		}
	}
}