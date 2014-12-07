package net.dorokhov.pony.core.user.exception;

public class InvalidCredentialsException extends Exception {

	public InvalidCredentialsException() {
		super("Credentials are invalid.");
	}

}
