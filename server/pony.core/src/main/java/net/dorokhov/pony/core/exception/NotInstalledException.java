package net.dorokhov.pony.core.exception;

import java.io.Serializable;

public class NotInstalledException extends RuntimeException implements Serializable {

	public NotInstalledException() {
		super("Not installed.");
	}

}
