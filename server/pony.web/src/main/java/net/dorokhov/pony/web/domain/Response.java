package net.dorokhov.pony.web.domain;

import java.io.Serializable;

public class Response implements Serializable {

	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean aSuccessful) {
		successful = aSuccessful;
	}

	public Response() {}

	public Response(boolean aSuccessful) {
		successful = aSuccessful;
	}
}
