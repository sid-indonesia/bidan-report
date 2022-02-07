package org.sidindonesia.bidanreport.web.response;

import java.util.List;

import lombok.Data;

@Data
public class BundleTransactionResponse {
	private List<EntryComponent> entry;

	@Data
	public static class EntryComponent {
		private EntryResponseComponent response;

		@Data
		public static class EntryResponseComponent {
			private String location;
		}
	}

	public EntryComponent getEntryFirstRep() {
		return entry.get(0);
	}
}
