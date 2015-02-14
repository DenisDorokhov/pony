package net.dorokhov.pony.web.client.util;

public class ObjectUtils {

	public static boolean nullSafeEquals(Object o1, Object o2) {

		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}

		return o1.equals(o2);
	}

	public static String nullSafeToString(Object aObject) {
		return aObject != null ? aObject.toString() : null;
	}

}
