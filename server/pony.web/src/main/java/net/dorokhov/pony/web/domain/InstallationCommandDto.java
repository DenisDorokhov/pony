package net.dorokhov.pony.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstallationCommandDto implements Serializable {

	private String adminLogin;

	private String adminPassword;

	private List<String> libraryFolders;

	public String getAdminLogin() {
		return adminLogin;
	}

	public void setAdminLogin(String aAdminLogin) {
		adminLogin = aAdminLogin;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String aAdminPassword) {
		adminPassword = aAdminPassword;
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
