package net.dorokhov.pony.core.user.exception;

public class SelfRoleModificationException extends Exception {

	private final Long userId;

	public SelfRoleModificationException(Long aUserId) {

		super("User cannot modify roles of itself.");

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}

}
