package org.sidindonesia.bidanreport.integration.qontak.web.request;

import lombok.Data;

@Data
public class AuthRequest {
	private String client_id;
	private String client_secret;
	private String grant_type = "password";
	private String username;
	private String password;
}
