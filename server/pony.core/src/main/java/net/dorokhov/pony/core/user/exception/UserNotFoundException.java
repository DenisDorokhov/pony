package net.dorokhov.pony.core.user.exception;

public class UserNotFoundException extends Exception {

	private Long userId;

	public UserNotFoundException(Long aUserId) {

		super("User [" + aUserId + "] not found.");

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}
}
