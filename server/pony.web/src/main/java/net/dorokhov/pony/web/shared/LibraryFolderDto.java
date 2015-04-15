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

}
