package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class UserExistsException extends Exception implements Serializable {

	private String email;

	public UserExistsException(String aEmail) {

		super("User [" + aEmail + "] already exists.");

		email = aEmail;
	}

	public String getEmail() {
		return email;
	}
}
