package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class InvalidTokenException extends RuntimeException implements Serializable {

	public InvalidTokenException() {
		super("Token is invalid.");
	}
}
