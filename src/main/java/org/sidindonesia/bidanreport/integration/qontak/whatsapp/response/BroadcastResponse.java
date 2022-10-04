package org.sidindonesia.bidanreport.integration.qontak.whatsapp.response;

import java.util.List;

import lombok.Data;

@Data
public class BroadcastResponse {
	private String status;
	private DataObj data;
	private ErrorObj error;

	@Data
	public static class DataObj {
		private String id;
		private String send_at;
		private String created_at;
		private MessageStatusCount message_status_count;

		@Data
		public static class MessageStatusCount {
			private Integer failed;
			private Integer delivered;
			private Integer read;
			private Integer pending;
			private Integer sent;
		}
	}

	@Data
	public static class ErrorObj {
		private Integer code;
		private List<Object> messages;
	}

	public BroadcastResponse withStatus(int rawStatusCode) {
		this.status = String.valueOf(rawStatusCode);
		return this;
	}
}