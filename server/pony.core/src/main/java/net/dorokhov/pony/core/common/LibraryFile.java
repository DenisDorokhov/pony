package net.dorokhov.pony.core.common;

import java.io.File;

public class LibraryFile {

	private final File file;

	private final FileType type;

	private LibraryFolder parentFolder;

	public LibraryFile(File aFile, FileType aType, LibraryFolder aParentFolder) {

		if (aFile == null) {
			throw new NullPointerException("File cannot be null.");
		}
		if (aType == null) {
			throw new NullPointerException("Type cannot be null.");
		}

		file = aFile;
		type = aType;

		setParentFolder(aParentFolder);
	}

	public File getFile() {
		return file;
	}

	public FileType getType() {
		return type;
	}

	public LibraryFolder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(LibraryFolder aParentFolder) {
		parentFolder = aParentFolder;
	}
}
