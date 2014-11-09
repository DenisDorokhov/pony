package net.dorokhov.pony.core.library.exception;

import java.io.File;

public class FileNotFoundException extends RuntimeException {

	private File file;

	public FileNotFoundException(File aFile) {

		super("File '" + aFile.getPath() + "' must exist.");

		file = aFile;
	}

	public File getFile() {
		return file;
	}
}
