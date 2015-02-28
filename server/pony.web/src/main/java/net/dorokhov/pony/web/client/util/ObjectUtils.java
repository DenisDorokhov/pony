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

	public static <T extends Comparable<? super T>> int compare(final T c1, final T c2) {

		if (c1 == c2) {
			return 0;
		} else if (c1 == null) {
			return -1;
		} else if (c2 == null) {
			return 1;
		}

		return c1.compareTo(c2);
	}

}
