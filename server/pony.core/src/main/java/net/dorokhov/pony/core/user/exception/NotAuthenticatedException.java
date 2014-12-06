package net.dorokhov.pony.core.user.exception;

public class NotAuthenticatedException extends RuntimeException {

	public NotAuthenticatedException() {
		super("Authenticated user could not be found.");
	}

}
