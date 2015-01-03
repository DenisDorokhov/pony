package net.dorokhov.pony.web.domain.command;

import net.dorokhov.pony.web.validation.FolderExists;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class InstallCommandDto {

	private String userName;

	private String userEmail;

	private String userPassword;

	private List<LibraryFolder> libraryFolders;

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

	@Valid
	public List<LibraryFolder> getLibraryFolders() {

		if (libraryFolders == null) {
			libraryFolders = new ArrayList<>();
		}

		return libraryFolders;
	}

	public void setLibraryFolders(List<LibraryFolder> aLibraryFolders) {
		libraryFolders = aLibraryFolders;
	}

	public static class LibraryFolder {

		private String path;

		@FolderExists
		public String getPath() {
			return path;
		}

		public void setPath(String aPath) {
			path = aPath;
		}
	}

}
