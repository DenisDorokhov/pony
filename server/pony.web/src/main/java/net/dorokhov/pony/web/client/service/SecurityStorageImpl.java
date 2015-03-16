package net.dorokhov.pony.web.client.service;

import com.google.gwt.storage.client.Storage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SecurityStorageImpl implements SecurityStorage {

	private static final String STORAGE_ACCESS_TOKEN_KEY = "AuthenticationStatusProvider.accessToken";
	private static final String STORAGE_ACCESS_TOKEN_EXPIRATION_KEY = "AuthenticationStatusProvider.accessTokenExpiration";
	private static final String STORAGE_REFRESH_TOKEN_KEY = "AuthenticationStatusProvider.refreshToken";

	private final Map<String, String> storageFallBack = new HashMap<>();

	private final Storage storage;

	public SecurityStorageImpl() {
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

		String value;

		if (storage != null) {
			value = storage.getItem(aKey);
		} else {
			value = storageFallBack.get(aKey);
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
		} else {
			if (aValue != null) {
				storageFallBack.put(aKey, aValue);
			} else {
				storageFallBack.remove(aKey);
			}
		}
	}

}
