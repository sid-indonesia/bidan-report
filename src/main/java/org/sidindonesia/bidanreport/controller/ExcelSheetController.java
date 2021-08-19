package org.sidindonesia.bidanreport.controller;

import org.sidindonesia.bidanreport.service.ExcelSheetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/ExcelSheet")
public class ExcelSheetController {

	private final ExcelSheetService excelSheetService;

	@Value("${spring.jpa.properties.hibernate.default_schema:sid}")
	private String schemaName;

	@GetMapping("/$download")
	public ResponseEntity<Resource> downloadAllTablesAsExcelSheets() {
		String filename = schemaName + ".xlsx";
		log.debug("REST request to get all tables in schema `" + schemaName + "` as Excel Sheets");
		InputStreamResource file = new InputStreamResource(excelSheetService.downloadAllTablesAsExcelSheets());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
			.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
	}
}
