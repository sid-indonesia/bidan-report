package org.sidindonesia.bidanreport.integration.qontak.web.response;

import java.util.List;

import lombok.Data;

@Data
public class FileUploadResponse {
	private String status;
	private DataObj data;
	private ErrorObj error;

	@Data
	public static class DataObj {
		private String filename;
		private String url;
	}

	@Data
	public static class ErrorObj {
		private Integer code;
		private List<Object> messages;
	}
}