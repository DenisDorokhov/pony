package net.dorokhov.pony.web.client.service;

import com.google.gwt.storage.client.Storage;
import net.dorokhov.pony.web.client.common.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.common.OperationCallback;
import net.dorokhov.pony.web.client.common.OperationRequest;
import net.dorokhov.pony.web.client.common.RequestAdapter;
import net.dorokhov.pony.web.shared.*;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class AuthenticationManager {

	private static final String STORAGE_TOKEN_KEY = "AuthenticationStatusProvider.token";

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	private Storage storage;

	private String token;

	private UserDto user;

	@Inject
	public AuthenticationManager(ApiService aApiService) {

		apiService = aApiService;

		storage = Storage.getLocalStorageIfSupported();
	}

	public boolean isAuthenticated() {
		return getUser() != null;
	}

	public String getToken() {

		if (token == null && storage != null) {
			token = storage.getItem(STORAGE_TOKEN_KEY);
		}

		return token;
	}

	public UserDto getUser() {
		return user;
	}

	public OperationRequest updateStatus(final OperationCallback<UserDto> aCallback) {

		log.info("Updating authentication status...");

		return new RequestAdapter(apiService.getCurrentUser(new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				setUser(aUser);

				log.info("Current user is [" + aUser.getEmail() + "].");

				aCallback.onSuccess(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				setToken(null);
				setUser(null);

				log.info("Could not update authentication status.");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest authenticate(final CredentialsDto aCredentials, final OperationCallback<UserDto> aCallback) {

		log.info("Authenticating user [" + aCredentials.getEmail() + "]...");

		return new RequestAdapter(apiService.authenticate(aCredentials, new MethodCallbackAdapter<>(new OperationCallback<AuthenticationDto>() {
			@Override
			public void onSuccess(AuthenticationDto aAuthentication) {

				setToken(aAuthentication.getToken());
				setUser(aAuthentication.getUser());

				log.info("User [" + aAuthentication.getUser().getEmail() + "] has authenticated.");

				aCallback.onSuccess(aAuthentication.getUser());
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Authentication failed for user [" + aCredentials.getEmail() + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest logout(final OperationCallback<Void> aCallback) {

		log.info("Logging out...");

		OperationRequest request = new RequestAdapter(apiService.logout(new MethodCallbackAdapter<>(new OperationCallback<Object>() {
			@Override
			public void onSuccess(Object aData) {

				log.info("User has logged out.");

				aCallback.onSuccess(null);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Logging out failed.");

				aCallback.onError(aErrors);
			}
		})));

		setToken(null);
		setUser(null);

		return request;
	}

	private void setToken(String aToken) {

		token = aToken;

		if (storage != null) {
			if (token != null) {
				storage.setItem(STORAGE_TOKEN_KEY, token);
			} else {
				storage.removeItem(STORAGE_TOKEN_KEY);
			}
		}
	}

	private void setUser(UserDto aUser) {
		user = aUser;
	}

}
