package org.sidindonesia.bidanreport.controller.response;

import lombok.Data;

@Data
public class QontakWhatsAppAuthResponse {
	private String access_token;
	private String token_type;
	private Long expires_in;
	private String refresh_token;
	private Long created_at;
}
// {"access_token":"AAoJ1RCKCu4MLCq_JEkIw1pN_smnmpfVIwYbZGrjk5E",
//"token_type":"bearer",
//"expires_in":31556952,
//"refresh_token":"dUf7pQhUsQro-0yEULG51FSPxbmLGMrNY2x2RPSgbTQ",
//"created_at":1594349114}