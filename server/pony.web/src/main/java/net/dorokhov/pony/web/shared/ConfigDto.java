package net.dorokhov.pony.web.shared;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ConfigDto {

	private Integer autoScanInterval;

	private List<String> libraryFolders;

	@NotNull
	public Integer getAutoScanInterval() {
		return autoScanInterval;
	}

	public void setAutoScanInterval(Integer aAutoScanInterval) {
		autoScanInterval = aAutoScanInterval;
	}

	public List<String> getLibraryFolders() {

		if (libraryFolders == null) {
			libraryFolders = new ArrayList<>();
		}

		return libraryFolders;
	}

	public void setLibraryFolders(List<String> aLibraryFolders) {
		libraryFolders = aLibraryFolders;
	}

}
