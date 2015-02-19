package net.dorokhov.pony.web.client.util;

import net.dorokhov.pony.web.client.message.Errors;
import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

public class ErrorUtils {

	public static ErrorDto getErrorByCode(List<ErrorDto> aErrors, String ... aCodes) {

		List<ErrorDto> result = getErrorsByCode(aErrors, aCodes);

		return result.size() > 0 ? result.get(0) : null;
	}

	public static List<ErrorDto> getErrorsByCode(List<ErrorDto> aErrors, String ... aCodes) {

		List<ErrorDto> result = new ArrayList<>();

		for (ErrorDto error : aErrors) {
			for (String code : aCodes) {
				if (error.getCode().equals(code) || error.getCode().startsWith(code + ".")) {
					result.add(error);
				}
			}
		}

		return result;
	}

	public static String formatError(ErrorDto aError) {

		String result = null;

		//noinspection EmptyCatchBlock
		try {
			result = Errors.INSTANCE.getString(errorCodeToMethod(aError.getCode()));
		} catch (MissingResourceException e) {}

		if (result == null) {
			return aError.getText();
		}

		for (int i = 0; i < aError.getArguments().size(); i++) {
			result = result.replaceAll("\\{" + i + "\\}", aError.getArguments().get(i));
		}

		return result;
	}

	private static String errorCodeToMethod(String aErrorCode) {

		String method = "";

		for (String part : aErrorCode.split("\\.")) {
			if (method.length() == 0) {
				method += part;
			} else if (part.length() > 0) {
				method += part.substring(0, 1).toUpperCase() + part.substring(1);
			}
		}

		return method;
	}

}
