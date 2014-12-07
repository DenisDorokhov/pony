package net.dorokhov.pony.core.library.exception;

import java.io.File;

public class FileNotFoundException extends Exception {

	private File file;

	public FileNotFoundException(File aFile) {

		super("File '" + aFile.getPath() + "' must exist.");

		file = aFile;
	}

	public File getFile() {
		return file;
	}
}
