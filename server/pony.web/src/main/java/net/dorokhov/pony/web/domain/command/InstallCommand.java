package net.dorokhov.pony.web.domain.command;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstallCommand implements Serializable {

	private String userName;

	private String userEmail;

	private String userPassword;

	private List<String> libraryFolders;

	@NotBlank
	@Size(max = 255)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String aUserName) {
		userName = aUserName;
	}

	@NotBlank
	@Email
	@Size(max = 255)
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String aUserEmail) {
		userEmail = aUserEmail;
	}

	@NotBlank
	@Size(max = 255)
	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String aUserPassword) {
		userPassword = aUserPassword;
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
