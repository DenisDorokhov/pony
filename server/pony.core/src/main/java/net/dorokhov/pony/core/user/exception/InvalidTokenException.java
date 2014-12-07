package net.dorokhov.pony.core.user.exception;

public class InvalidTokenException extends Exception {

	public InvalidTokenException() {
		super("Token is invalid.");
	}

}
