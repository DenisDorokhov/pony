package net.dorokhov.pony.core.user.exception;

public class UserNotFoundException extends Exception {

	private Long id;

	public UserNotFoundException(Long aId) {

		super("User [" + aId + "] not found.");

		id = aId;
	}

	public Long getId() {
		return id;
	}
}
