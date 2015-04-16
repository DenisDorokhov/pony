package net.dorokhov.pony.web.shared;

import net.dorokhov.pony.web.server.validation.FolderExists;

public class LibraryFolderDto {

	private String path;

	public LibraryFolderDto() {}

	public LibraryFolderDto(String aPath) {
		path = aPath;
	}

	@FolderExists
	public String getPath() {
		return path;
	}

	public void setPath(String aPath) {
		path = aPath;
	}

	@Override
	public int hashCode() {
		return path != null ? path.hashCode() : 0;
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && getClass().equals(aObj.getClass())) {

			LibraryFolderDto that = (LibraryFolderDto) aObj;

			return path.equals(that.path);
		}

		return false;
	}

}
