package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class InvalidPasswordException extends Exception implements Serializable {

	public InvalidPasswordException() {
		super("Password is invalid.");
	}
}
