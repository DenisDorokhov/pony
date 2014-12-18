package net.dorokhov.pony.core.user.exception;

public class UserSelfDeletionException extends Exception {

	private Long userId;

	public UserSelfDeletionException(Long aUserId) {

		super("User [" + aUserId + "] must not delete itself.");

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}

}
