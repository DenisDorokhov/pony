package net.dorokhov.pony.core.common;

public class PonyUtils {

	public static String sanitizeFileName(String aFileName) {
		return aFileName.replaceAll("[^\\p{L}0-9.\\- ]", "_").trim();
	}

}
