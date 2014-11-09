package net.dorokhov.pony.core.library.exception;

import java.io.File;

public class NotSongException extends RuntimeException {

	private File file;

	public NotSongException(File aFile) {

		super("File '" + aFile.getPath() + "' must be a song.");

		file = aFile;
	}

	public File getFile() {
		return file;
	}
}
