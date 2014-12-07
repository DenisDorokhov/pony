package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class InvalidCredentialsException extends Exception implements Serializable {

	public InvalidCredentialsException() {
		super("Credentials are invalid.");
	}
}
