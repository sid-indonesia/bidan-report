package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import lombok.Data;

@Data
public class BroadcastDirectRequest {
	private String to_number;
	private String to_name;
	private String message_template_id;
	private String channel_integration_id;
	private Language language = new Language();
	private Parameters parameters;
}
