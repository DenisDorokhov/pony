package net.dorokhov.pony.web.client.util;

import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.ArrayList;
import java.util.List;

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

}
