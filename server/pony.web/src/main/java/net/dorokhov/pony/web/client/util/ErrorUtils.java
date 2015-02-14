package net.dorokhov.pony.web.client.util;

import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.ArrayList;
import java.util.List;

public class ErrorUtils {

	public static ErrorDto getErrorByCode(String aCode, List<ErrorDto> aErrors) {

		List<ErrorDto> result = getErrorsByCode(aCode, aErrors);

		return result.size() > 0 ? result.get(0) : null;
	}

	public static List<ErrorDto> getErrorsByCode(String aCode, List<ErrorDto> aErrors) {

		List<ErrorDto> result = new ArrayList<>();

		for (ErrorDto error : aErrors) {
			if (error.getCode().equals(aCode) || error.getCode().startsWith(aCode + ".")) {
				result.add(error);
			}
		}

		return result;
	}

}
