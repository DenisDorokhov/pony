package net.dorokhov.pony.core.library.exception;

import java.io.File;

public class NotFolderException extends Exception {

	private File file;

	public NotFolderException(File aFile) {

		super("File [" + aFile.getPath() + "] must be a folder.");

		file = aFile;
	}

	public File getFile() {
		return file;
	}

}
