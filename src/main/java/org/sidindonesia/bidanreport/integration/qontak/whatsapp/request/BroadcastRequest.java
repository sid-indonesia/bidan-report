package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class BroadcastRequest {
	private String to_number;
	private String to_name;
	private String message_template_id;
	private String channel_integration_id;
	private Language language = new Language();
	private Parameters parameters;

	@Data
	public static class Language {
		private String code = "id";
	}

	@Data
	public static class Parameters {
		private Set<Body> body = new HashSet<>();

		@Data
		public static class Body {
			private String key;
			private String value;
			private String value_text;
		}

		public void addBodyWithValues(String key, String value, String value_text) {
			Body newBody = new Body();
			newBody.setKey(key);
			newBody.setValue(value);
			newBody.setValue_text(value_text);
			this.body.add(newBody);
		}
	}
}
