package net.dorokhov.pony.core.common;

import java.io.File;

public class LibraryImage extends LibraryFile {

	public LibraryImage(File aFile) {
		super(aFile);
	}

	public LibraryImage(File aFile, LibraryFolder aParentFolder) {
		super(aFile, aParentFolder);
	}

}
