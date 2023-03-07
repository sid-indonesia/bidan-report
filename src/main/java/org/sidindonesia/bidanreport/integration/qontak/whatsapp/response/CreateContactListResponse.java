package org.sidindonesia.bidanreport.integration.qontak.whatsapp.response;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class CreateContactListResponse {
	private String status;
	private DataObj data;
	private ErrorObj error;

	@Data
	public static class DataObj {
		private String id;
		private Set<String> contact_variables;
		private String send_at;
		private String created_at;
	}

	@Data
	public static class ErrorObj {
		private Integer code;
		private List<Object> messages;
	}

	public CreateContactListResponse withStatus(int rawStatusCode) {
		this.status = String.valueOf(rawStatusCode);
		return this;
	}
}