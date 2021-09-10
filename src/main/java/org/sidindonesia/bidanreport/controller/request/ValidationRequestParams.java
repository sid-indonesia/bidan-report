package org.sidindonesia.bidanreport.controller.request;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ValidationRequestParams {

	private String fromDateString;
	private String untilDateString;

	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
		this.fromDate = LocalDateTime.parse(fromDateString);
	}

	public void setUntilDateString(String untilDateString) {
		this.untilDateString = untilDateString;
		this.untilDate = LocalDateTime.parse(untilDateString);
	}

//  TODO change the request param to @RequestBody instead of @ModelAttribute and update UI to create POST request and send JSON request body
	@NotNull(message = "fromDate is required")
	private LocalDateTime fromDate;
	@NotNull(message = "untilDate is required")
	private LocalDateTime untilDate;

	private Set<Table> tables = new HashSet<>();

	@Data
	public static class Table {
		@NotBlank
		private String name;
		private Set<String> columns = new HashSet<>();
	}
}
