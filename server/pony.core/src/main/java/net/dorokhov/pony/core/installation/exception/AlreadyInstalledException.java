package net.dorokhov.pony.core.installation.exception;

public class AlreadyInstalledException extends Exception {

	public AlreadyInstalledException() {
		super("Already installed.");
	}

}
