package net.dorokhov.pony.web.shared.command;

import net.dorokhov.pony.web.server.validation.RepeatPassword;
import net.dorokhov.pony.web.shared.LibraryFolderDto;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@RepeatPassword
public class InstallCommandDto {

	private String userName;

	private String userEmail;

	private String userPassword;

	private String userRepeatPassword;

	private List<LibraryFolderDto> libraryFolders;

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

	public String getUserRepeatPassword() {
		return userRepeatPassword;
	}

	public void setUserRepeatPassword(String aUserRepeatPassword) {
		userRepeatPassword = aUserRepeatPassword;
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
