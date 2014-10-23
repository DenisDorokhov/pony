package net.dorokhov.pony.core.installation.exception;

import java.io.Serializable;

public class AlreadyInstalledException extends RuntimeException implements Serializable {

	public AlreadyInstalledException() {
		super("Already installed.");
	}

}
