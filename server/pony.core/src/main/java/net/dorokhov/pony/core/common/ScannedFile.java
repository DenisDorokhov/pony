package net.dorokhov.pony.core.common;

import java.io.File;

public class ScannedFile {

	public static enum Type {
		SONG, IMAGE
	}

	private File file;

	private Type type;

	public ScannedFile(File aFile, Type aType) {

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

	public Type getType() {
		return type;
	}
}
