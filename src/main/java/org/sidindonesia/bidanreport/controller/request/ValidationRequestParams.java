package org.sidindonesia.bidanreport.controller.request;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ValidationRequestParams {

	@NotNull
	private LocalDateTime fromDate;
	@NotNull
	private LocalDateTime untilDate;
	private Set<Table> tables = new HashSet<>();

	@Data
	public static class Table {
		@NotBlank
		private String name;
		private Set<String> columns = new HashSet<>();
	}
}
