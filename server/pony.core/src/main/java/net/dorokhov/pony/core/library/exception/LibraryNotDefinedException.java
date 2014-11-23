package net.dorokhov.pony.core.library.exception;

public class LibraryNotDefinedException extends RuntimeException {

	public LibraryNotDefinedException() {
		super("Library not defined.");
	}

}
