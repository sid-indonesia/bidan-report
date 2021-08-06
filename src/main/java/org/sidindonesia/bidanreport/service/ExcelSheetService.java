package org.sidindonesia.bidanreport.service;

import java.io.ByteArrayInputStream;

import org.sidindonesia.bidanreport.repository.BasicEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ExcelSheetService {

	private final BasicEntityRepository basicEntityRepository;

	public ByteArrayInputStream downloadAllTablesAsExcelSheets() {
		// TODO Auto-generated method stub
		return null;
	}
}
