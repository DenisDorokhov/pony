package net.dorokhov.pony.core.domain;

public class UserToken {

	private String id;

	public UserToken(String aId) {

		if (aId == null) {
			throw new IllegalArgumentException("Token identifier must not be null.");
		}

		id = aId;
	}

	public String getId() {
		return id;
	}
}
