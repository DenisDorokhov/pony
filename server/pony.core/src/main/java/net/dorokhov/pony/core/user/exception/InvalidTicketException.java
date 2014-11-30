package net.dorokhov.pony.core.user.exception;

import java.io.Serializable;

public class InvalidTicketException extends RuntimeException implements Serializable {

	private String id;

	public InvalidTicketException(String aId) {

		super("Ticket [" + aId + "] is invalid.");

		id = aId;
	}

	public String getId() {
		return id;
	}
}
