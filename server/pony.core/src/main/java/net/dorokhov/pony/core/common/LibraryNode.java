package net.dorokhov.pony.core.common;

import java.io.File;

public abstract class LibraryNode {

	private final File file;

	private LibraryFolder parentFolder;

	protected LibraryNode(File aFile) {
		this(aFile, null);
	}

	protected LibraryNode(File aFile, LibraryFolder aParentFolder) {

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

			LibraryNode that = (LibraryNode) aObj;

			return file.equals(that.file);
		}

		return false;
	}
}
