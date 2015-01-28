package net.dorokhov.pony.web.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvalidArgumentException extends Exception {

	private String errorCode;

	private List<String> arguments;

	public InvalidArgumentException(String aErrorCode, String aMessage) {

		super(aMessage);

		errorCode = aErrorCode;
	}

	public InvalidArgumentException(String aErrorCode, String aMessage, String... aArguments) {

		super(aMessage);

		errorCode = aErrorCode;
		arguments = Arrays.asList(aArguments);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public List<String> getArguments() {

		if (arguments == null) {
			arguments = new ArrayList<>();
		}

		return arguments;
	}
}
