package net.dorokhov.pony.core.common;

import java.io.File;

public abstract class LibraryFile extends LibraryNode {

	private String mimeType;

	public LibraryFile(File aFile) {
		this(aFile, null);
	}

	public LibraryFile(File aFile, LibraryFolder aParentFolder) {
		super(aFile, aParentFolder);
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

}
