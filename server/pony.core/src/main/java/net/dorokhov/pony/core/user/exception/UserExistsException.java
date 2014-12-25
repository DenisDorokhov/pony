package net.dorokhov.pony.core.user.exception;

public class UserExistsException extends Exception {

	private String email;

	public UserExistsException(String aEmail) {

		super("User [" + aEmail + "] already exists.");

		email = aEmail;
	}

	public String getEmail() {
		return email;
	}

}
