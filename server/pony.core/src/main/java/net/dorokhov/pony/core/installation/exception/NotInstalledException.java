package net.dorokhov.pony.core.installation.exception;

import java.io.Serializable;

public class NotInstalledException extends RuntimeException implements Serializable {

	public NotInstalledException() {
		super("Not installed.");
	}

}
