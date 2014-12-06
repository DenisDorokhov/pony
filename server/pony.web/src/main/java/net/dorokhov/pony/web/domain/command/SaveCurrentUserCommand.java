package net.dorokhov.pony.web.domain.command;

import net.dorokhov.pony.web.domain.UserDto;

import java.io.Serializable;

public class SaveCurrentUserCommand implements Serializable {

	private UserDto user;

	private String oldPassword;

	private String newPassword;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto aUser) {
		user = aUser;
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
