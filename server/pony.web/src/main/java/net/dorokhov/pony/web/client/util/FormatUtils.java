package net.dorokhov.pony.web.client.util;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import net.dorokhov.pony.web.client.resource.Errors;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.LogMessageDto;

import java.util.List;
import java.util.MissingResourceException;

public class FormatUtils {

	public static String formatError(ErrorDto aError) {
		return FormatUtils.formatMessage(Errors.INSTANCE, aError.getCode(), aError.getArguments(), aError.getText());
	}

	public static String formatLog(LogMessageDto aLogMessage) {
		return FormatUtils.formatMessage(Errors.INSTANCE, aLogMessage.getCode(), aLogMessage.getArguments(), aLogMessage.getText());
	}

	public static String formatMessage(ConstantsWithLookup aConstants, String aCode, List<String> aArguments, String aText) {

		String result = null;

		try {
			result = aConstants.getString(codeToMethod(aCode));
		} catch (MissingResourceException ignored) {}

		if (result == null) {
			return aText;
		}

		for (int i = 0; i < aArguments.size(); i++) {
			result = result.replaceAll("\\{" + i + "\\}", aArguments.get(i));
		}

		return result;
	}

	private static String codeToMethod(String aCode) {

		String method = "";

		for (String part : aCode.split("\\.")) {
			if (method.length() == 0) {
				method += part;
			} else if (part.length() > 0) {
				method += part.substring(0, 1).toUpperCase() + part.substring(1);
			}
		}

		return method;
	}

}
