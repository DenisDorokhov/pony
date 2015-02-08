package net.dorokhov.pony.web.client.common;

import net.dorokhov.pony.web.shared.AuthenticationDto;
import net.dorokhov.pony.web.shared.UserDto;

public class AuthenticationStatus {

	private String token;

	private UserDto currentUser;

	public boolean isAuthenticated() {
		return token != null;
	}

	public String getToken() {
		return token;
	}

	public UserDto getCurrentUser() {
		return currentUser;
	}

	public void updateCurrentUser(UserDto aUser) {
		currentUser = aUser;
	}

	public void updateAuthentication(AuthenticationDto aAuthentication) {
		token = aAuthentication.getToken();
		currentUser = aAuthentication.getUser();
	}

	public void clearAuthentication() {
		token = null;
		currentUser = null;
	}

}
