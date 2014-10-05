package net.dorokhov.pony.core.exception;

public class FileIsDirectoryException extends RuntimeException {

	public FileIsDirectoryException() {
		super("File must not be a directory.");
	}
}
