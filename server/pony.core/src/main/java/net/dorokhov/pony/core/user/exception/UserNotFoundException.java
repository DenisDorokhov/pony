package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class UserNotFoundException extends Exception implements Serializable {

	private Long id;

	public UserNotFoundException(Long aId) {

		super("User [" + aId + "] not found.");

		id = aId;
	}

	public Long getId() {
		return id;
	}
}
