package net.dorokhov.pony.core.library.exception;

public class ConcurrentScanException extends Exception {

	public ConcurrentScanException() {
		super("Library is already scanning.");
	}

}
