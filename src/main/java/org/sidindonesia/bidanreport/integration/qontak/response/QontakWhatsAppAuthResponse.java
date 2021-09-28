package org.sidindonesia.bidanreport.integration.qontak.response;

import lombok.Data;

@Data
public class QontakWhatsAppAuthResponse {
	private String access_token;
	private String token_type;
	private Long expires_in;
	private String refresh_token;
	private Long created_at;
}