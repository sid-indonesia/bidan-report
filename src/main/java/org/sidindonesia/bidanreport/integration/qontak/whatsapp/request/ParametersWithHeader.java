package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParametersWithHeader extends Parameters {
	private Header header = new Header();

	@Data
	public static class Header {
		private String format = "IMAGE";
		private Set<Header.HeaderParam> params = new HashSet<>();

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