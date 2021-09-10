package org.sidindonesia.bidanreport.controller.request;

import lombok.Data;

@Data
public class QontakWhatsAppAuthRequest {
	private String client_id;
	private String client_secret;
	private String grant_type = "password";
	private String username;
	private String password;
}
