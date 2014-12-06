package net.dorokhov.pony.web.domain.command;

import java.io.Serializable;

public class UpdateCurrentUserCommand implements Serializable {

	private String name;

	private String email;

	private String oldPassword;

	private String newPassword;

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String aEmail) {
		email = aEmail;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String aOldPassword) {
		oldPassword = aOldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String aNewPassword) {
		newPassword = aNewPassword;
	}
}
