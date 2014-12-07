package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class NotAuthenticatedException extends Exception implements Serializable {

	public NotAuthenticatedException() {
		super("User is not authenticated.");
	}

}
