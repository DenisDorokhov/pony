package net.dorokhov.pony.core.user.exception;

public class NotAuthorizedException extends RuntimeException {

	public NotAuthorizedException() {
		super("User is not authorized to perform this action.");
	}

	public NotAuthorizedException(String message) {
		super(message);
	}
}
