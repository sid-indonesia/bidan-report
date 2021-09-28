package org.sidindonesia.bidanreport.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndonesiaPhoneNumberUtil {
	public static String sanitize(String maybeHasWrongNumberFormat) {
		return maybeHasWrongNumberFormat.charAt(0) == '0'
			? "62" + maybeHasWrongNumberFormat.substring(1, maybeHasWrongNumberFormat.length())
			: maybeHasWrongNumberFormat;
	}
}
