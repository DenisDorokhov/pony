package net.dorokhov.pony.core.common;

import java.io.File;

public abstract class LibraryFile {

	private final File file;

	private LibraryFolder parentFolder;

	private String mimeType;

	public LibraryFile(File aFile) {
		this(aFile, null);
	}

	public LibraryFile(File aFile, LibraryFolder aParentFolder) {

		if (aFile == null) {
			throw new NullPointerException("File cannot be null.");
		}

		file = aFile;
		parentFolder = aParentFolder;
	}

	public File getFile() {
		return file;
	}

	public LibraryFolder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(LibraryFolder aParentFolder) {
		parentFolder = aParentFolder;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && getClass().equals(aObj.getClass())) {

			LibraryFile that = (LibraryFile) aObj;

			return file.equals(that.file);
		}

		return false;
	}

}
