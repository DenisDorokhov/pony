package net.dorokhov.pony.core.user.exception;

public class InvalidPasswordException extends Exception {

	public InvalidPasswordException() {
		super("Password is invalid.");
	}

}
