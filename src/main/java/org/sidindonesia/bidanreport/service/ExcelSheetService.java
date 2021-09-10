package org.sidindonesia.bidanreport.service;

import java.io.ByteArrayInputStream;

import org.sidindonesia.bidanreport.controller.request.ValidationRequestParams;
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
	private static final String JPA_ENTITY_PACKAGE_NAME = AncClose.class.getPackageName();
	private static final String JPA_REPOSITORY_PACKAGE_NAME = AncCloseRepository.class.getPackageName();
	private final ApplicationContext context;

	public ByteArrayInputStream downloadAllTablesAsExcelSheets() {
		log.debug("Request to retrieve all tables as Excel Sheets");
		return ExcelHelper.allEntitiesToExcelSheets(context, JPA_ENTITY_PACKAGE_NAME, JPA_REPOSITORY_PACKAGE_NAME);
	}

	public ByteArrayInputStream validateThenRetrieveAsExcelSheets(ValidationRequestParams params) {
		log.debug("Request to validate column(s) is/are not null or blank then retrieve as ExcelSheets");

		if (params.getTables().isEmpty()) {
			return ExcelHelper.validateAllTableColumnsAreNotEmpty(context, JPA_ENTITY_PACKAGE_NAME,
				JPA_REPOSITORY_PACKAGE_NAME, params.getFromDate(), params.getUntilDate());
		} else {
			params.getTables().stream().forEach(table -> {
//				Class<?> clazz = ReflectionsUtil.getAllEntityClasses(JPA_ENTITY_PACKAGE_NAME).stream()
//					.filter(entityClass -> table.getName().equals(entityClass.getSimpleName())).findAny().orElseThrow();

				if (table.getColumns().isEmpty()) {
					// TODO validate all columns of the table
				} else {
					// TODO validate specified column(s) of the table
				}
			});

			return new ByteArrayInputStream(null);
		}
	}
}
