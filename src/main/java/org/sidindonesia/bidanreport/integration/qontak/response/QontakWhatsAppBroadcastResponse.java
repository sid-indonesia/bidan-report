package org.sidindonesia.bidanreport.integration.qontak.response;

import lombok.Data;

@Data
public class QontakWhatsAppBroadcastResponse {
	private String status;
	private DataObj data;

	@Data
	public static class DataObj {
		private String id;
		private String send_at;
		private String created_at;
	}
}