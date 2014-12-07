package net.dorokhov.pony.core.user.exception;

public class NotAuthorizedException extends Exception {

	public NotAuthorizedException() {
		super("User is not authorized to perform this action.");
	}

	public NotAuthorizedException(String message) {
		super(message);
	}
}
