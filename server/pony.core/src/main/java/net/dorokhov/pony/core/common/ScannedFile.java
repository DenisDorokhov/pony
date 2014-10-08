package net.dorokhov.pony.core.common;

import java.io.File;

public class ScannedFile {

	private File file;

	private FileType type;

	public ScannedFile(File aFile, FileType aType) {

		if (aFile == null) {
			throw new NullPointerException();
		}
		if (aType == null) {
			throw new NullPointerException();
		}

		file = aFile;
		type = aType;
	}

	public File getFile() {
		return file;
	}

	public FileType getType() {
		return type;
	}
}
