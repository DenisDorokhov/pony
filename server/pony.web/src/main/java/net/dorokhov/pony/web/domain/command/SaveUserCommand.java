package net.dorokhov.pony.web.domain.command;

import net.dorokhov.pony.web.domain.UserDto;

import java.io.Serializable;

public class SaveUserCommand implements Serializable {

	private UserDto user;

	private String password;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto aUser) {
		user = aUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String aPassword) {
		password = aPassword;
	}
}
