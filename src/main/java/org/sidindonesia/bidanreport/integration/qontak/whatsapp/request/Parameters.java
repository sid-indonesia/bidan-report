package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Parameters {
	private Set<Body> body = new HashSet<>();

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class BodyWithValueText extends Body {
		private String value_text;
	}

	@Data
	public static class Body {
		private String key;
		private String value;
	}

	public void addBodyWithValues(String key, String value, String value_text) {
		BodyWithValueText newBody = new BodyWithValueText();
		newBody.setKey(key);
		newBody.setValue(value);
		newBody.setValue_text(value_text);
		this.body.add(newBody);
	}

	public void addBodyWithValues(String key, String value) {
		Body newBody = new Body();
		newBody.setKey(key);
		newBody.setValue(value);
		this.body.add(newBody);
	}
}