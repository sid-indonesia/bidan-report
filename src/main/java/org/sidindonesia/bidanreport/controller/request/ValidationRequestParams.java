package org.sidindonesia.bidanreport.controller.request;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ValidationRequestParams {

	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	private Set<Table> tables;

	@Data
	public static class Table {
		@NotBlank
		private String name;
		private Set<String> columns;
	}
}
