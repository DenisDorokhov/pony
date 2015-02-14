package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.Errors;
import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.MissingResourceException;
import java.util.logging.Logger;

public class ErrorFormatter {

	private final Logger log = Logger.getLogger(getClass().getName());

	public String formatError(ErrorDto aError) {

		String result = null;

		try {
			result = Errors.INSTANCE.getString(errorCodeToMethod(aError.getCode()));
		} catch (MissingResourceException e) {
			log.fine("Could not find string [" + aError.getCode() + "], using default error text.");
		}

		if (result == null) {
			return aError.getText();
		}

		for (int i = 0; i < aError.getArguments().size(); i++) {
			result = result.replaceAll("\\{" + i + "\\}", aError.getArguments().get(i));
		}

		return result;
	}

	private String errorCodeToMethod(String aErrorCode) {

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
