package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InstallationCommand {

	private Integer autoScanInterval;

	private List<File> libraryFolders;

	private List<User> users;

	public Integer getAutoScanInterval() {
		return autoScanInterval;
	}

	public void setAutoScanInterval(Integer aAutoScanInterval) {
		autoScanInterval = aAutoScanInterval;
	}

	public List<File> getLibraryFolders() {

		if (libraryFolders == null) {
			libraryFolders = new ArrayList<>();
		}

		return libraryFolders;
	}

	public void setLibraryFolders(List<File> aLibraryFolders) {
		libraryFolders = aLibraryFolders;
	}

	public List<User> getUsers() {

		if (users == null) {
			users = new ArrayList<>();
		}

		return users;
	}

	public void setUsers(List<User> aUsers) {
		users = aUsers;
	}

}
