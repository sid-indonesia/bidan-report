package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class ParametersWithHeader extends Parameters {
		private Header header = new Header();

		@Data
		public static class Header {
			private String format = "IMAGE";
			private Set<HeaderParam> params = new HashSet<>();

			@Data
			public static class HeaderParam {
				private String key;
				private String value;
			}

			public void addHeaderParam(String key, String value) {
				HeaderParam headerParam = new HeaderParam();
				headerParam.setKey(key);
				headerParam.setValue(value);
				this.params.add(headerParam);
			}
		}
	}
}
