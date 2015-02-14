package net.dorokhov.pony.web.client.util;

public class StringUtils {

	public static boolean nullSafeNormalizedEquals(String aString1, String aString2) {

		if (aString1 == null && aString2 == null) {
			return true;
		}

		if (aString1 == null || aString2 == null) {
			return false;
		}

		String normalizedValue1 = aString1.trim().toLowerCase();
		String normalizedValue2 = aString2.trim().toLowerCase();

		return normalizedValue1.equals(normalizedValue2);
	}

	public static String secondsToMinutes(int aSeconds) {

		int minutes = aSeconds / 60;
		int seconds = aSeconds - minutes * 60;

		StringBuilder buf = new StringBuilder();

		buf.append(minutes).append(":");

		if (seconds <= 9) {
			buf.append("0");
		}
		buf.append(seconds);

		return buf.toString();
	}

	public static String secondsToHours(int aSeconds) {

		int hours = aSeconds / (60 * 60);
		int minutes = (aSeconds - hours * 60) / 60;
		int seconds = aSeconds - hours * 60 * 60 - minutes * 60;

		StringBuilder buf = new StringBuilder();

		buf.append(hours).append(":");

		if (minutes <= 9) {
			buf.append("0");
		}
		buf.append(minutes).append(":");

		if (seconds <= 9) {
			buf.append("0");
		}
		buf.append(seconds);

		return buf.toString();
	}
}
