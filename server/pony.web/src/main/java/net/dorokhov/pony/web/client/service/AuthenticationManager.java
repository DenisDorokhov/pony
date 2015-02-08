package net.dorokhov.pony.web.client.service;

import com.google.gwt.storage.client.Storage;
import net.dorokhov.pony.web.shared.AuthenticationDto;
import net.dorokhov.pony.web.shared.UserDto;

public class AuthenticationManager {

	private static String STORAGE_TOKEN_KEY = "AuthenticationStatusProvider.token";

	private Storage storage;

	private String token;

	private UserDto currentUser;

	public AuthenticationManager() {
		storage = Storage.getLocalStorageIfSupported();
	}

	public boolean isAuthenticated() {
		return getToken() != null;
	}

	public String getToken() {

		if (token == null && storage != null) {
			token = storage.getItem(STORAGE_TOKEN_KEY);
		}

		return token;
	}

	public UserDto getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserDto aCurrentUser) {
		currentUser = aCurrentUser;
	}

	public void authenticate(AuthenticationDto aAuthentication) {

		token = aAuthentication.getToken();

		if (storage != null) {
			storage.setItem(STORAGE_TOKEN_KEY, token);
		}

		currentUser = aAuthentication.getUser();
	}

	public void clearAuthentication() {
		token = null;
		currentUser = null;
	}

}
