package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class UserExistsException extends RuntimeException implements Serializable {

	private String username;

	public UserExistsException(String aUsername) {

		super("User [" + aUsername + "] already exists.");

		username = aUsername;
	}

	public String getUsername() {
		return username;
	}
}
