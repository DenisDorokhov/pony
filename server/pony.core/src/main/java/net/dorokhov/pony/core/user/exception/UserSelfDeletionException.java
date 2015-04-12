package net.dorokhov.pony.core.user.exception;

public class UserSelfDeletionException extends Exception {

	private final Long userId;

	public UserSelfDeletionException(Long aUserId) {

		super("User cannot delete itself.");

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}

}
