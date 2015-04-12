package net.dorokhov.pony.core.user.exception;

public class SelfDeletionException extends Exception {

	private final Long userId;

	public SelfDeletionException(Long aUserId) {

		super("User cannot delete itself.");

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}

}
