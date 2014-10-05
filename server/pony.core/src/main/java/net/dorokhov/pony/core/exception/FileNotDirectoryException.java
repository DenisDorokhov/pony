package net.dorokhov.pony.core.exception;

public class FileNotDirectoryException extends RuntimeException {

	public FileNotDirectoryException() {
		super("File must be a directory.");
	}
}
