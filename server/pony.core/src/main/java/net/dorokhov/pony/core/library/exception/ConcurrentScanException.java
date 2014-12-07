package net.dorokhov.pony.core.library.exception;

import java.io.Serializable;

public class ConcurrentScanException extends Exception implements Serializable {

	public ConcurrentScanException() {
		super("Library is already scanning.");
	}

}
