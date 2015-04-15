package net.dorokhov.pony.web.shared;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ConfigDto {

	private Integer autoScanInterval;

	private List<LibraryFolderDto> libraryFolders;

	public Integer getAutoScanInterval() {
		return autoScanInterval;
	}

	public void setAutoScanInterval(Integer aAutoScanInterval) {
		autoScanInterval = aAutoScanInterval;
	}

	@Valid
	public List<LibraryFolderDto> getLibraryFolders() {

		if (libraryFolders == null) {
			libraryFolders = new ArrayList<>();
		}

		return libraryFolders;
	}

	public void setLibraryFolders(List<LibraryFolderDto> aLibraryFolders) {
		libraryFolders = aLibraryFolders;
	}

}
