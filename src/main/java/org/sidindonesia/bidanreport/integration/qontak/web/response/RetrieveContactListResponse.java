package org.sidindonesia.bidanreport.integration.qontak.web.response;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class RetrieveContactListResponse {
	private String status;
	private DataObj data;
	private ErrorObj error;

	@Data
	public static class DataObj {
		private String id;
		private Set<String> contact_variables;
		private String created_at;
		private String finished_at;
		private String progress;
	}

	@Data
	public static class ErrorObj {
		private Integer code;
		private List<Object> messages;
	}
}