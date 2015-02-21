package net.dorokhov.pony.web.client.service;

import com.google.gwt.storage.client.Storage;

import java.util.Date;

public class AuthenticationStorage {

	public static final AuthenticationStorage INSTANCE = new AuthenticationStorage();

	private static final String STORAGE_ACCESS_TOKEN_KEY = "AuthenticationStatusProvider.accessToken";
	private static final String STORAGE_ACCESS_TOKEN_EXPIRATION_KEY = "AuthenticationStatusProvider.accessTokenExpiration";

	private static final String STORAGE_REFRESH_TOKEN_KEY = "AuthenticationStatusProvider.refreshToken";

	private final Storage storage;

	private AuthenticationStorage() {
		storage = Storage.getLocalStorageIfSupported();
	}

	public String getAccessToken() {
		return fetchValue(STORAGE_ACCESS_TOKEN_KEY);
	}

	public void setAccessToken(String aAccessToken) {
		storeValue(STORAGE_ACCESS_TOKEN_KEY, aAccessToken);
	}

	public Date getAccessTokenExpiration() {

		String value = fetchValue(STORAGE_ACCESS_TOKEN_EXPIRATION_KEY);

		return value != null ? new Date(Long.valueOf(value)) : null;
	}

	public void setAccessTokenExpiration(Date aAccessTokenExpiration) {
		storeValue(STORAGE_ACCESS_TOKEN_EXPIRATION_KEY, aAccessTokenExpiration != null ? String.valueOf(aAccessTokenExpiration.getTime()) : null);
	}

	public String getRefreshToken() {
		return fetchValue(STORAGE_REFRESH_TOKEN_KEY);
	}

	public void setRefreshToken(String aRefreshToken) {
		storeValue(STORAGE_REFRESH_TOKEN_KEY, aRefreshToken);
	}

	private String fetchValue(String aKey) {

		String value = null;

		if (storage != null) {
			value = storage.getItem(aKey);
		}

		return value;
	}

	private void storeValue(String aKey, String aValue) {
		if (storage != null) {
			if (aValue != null) {
				storage.setItem(aKey, aValue);
			} else {
				storage.removeItem(aKey);
			}
		}
	}

}
