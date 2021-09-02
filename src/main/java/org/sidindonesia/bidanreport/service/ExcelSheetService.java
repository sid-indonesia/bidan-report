package org.sidindonesia.bidanreport.service;

import java.io.ByteArrayInputStream;

import org.sidindonesia.bidanreport.domain.AncClose;
import org.sidindonesia.bidanreport.repository.AncCloseRepository;
import org.sidindonesia.jpatoexcel.helper.ExcelHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ExcelSheetService {
	private final ApplicationContext context;

	public ByteArrayInputStream downloadAllTablesAsExcelSheets() {
		log.debug("Request to retrieve all tables as Excel Sheets");
		return ExcelHelper.allEntitiesToExcelSheets(context, AncClose.class.getPackageName(),
			AncCloseRepository.class.getPackageName());
	}
}
