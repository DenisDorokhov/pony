package net.dorokhov.pony.core.user.exception;

public class NotAuthenticatedException extends Exception {

	public NotAuthenticatedException() {
		super("User is not authenticated.");
	}

}
