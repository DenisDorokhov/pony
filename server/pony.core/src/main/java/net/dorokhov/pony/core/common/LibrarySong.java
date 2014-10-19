package net.dorokhov.pony.core.common;

import java.io.File;

public class LibrarySong extends LibraryFile {

	public LibrarySong(File aFile) {
		super(aFile);
	}

	public LibrarySong(File aFile, LibraryFolder aParentFolder) {
		super(aFile, aParentFolder);
	}

}
