package net.dorokhov.pony.web.client.service;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.NoOpOperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.util.ErrorUtils;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.*;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

public class AuthenticationManager {

	public static interface Delegate {

		public void onInitialization(UserDto aUser);

		public void onAuthentication(UserDto aUser);

		public void onStatusUpdate(UserDto aUser);

		public void onLogout(UserDto aUser, boolean aExplicit);

	}

	public static abstract class NoOpDelegate implements Delegate {

		@Override
		public void onInitialization(UserDto aUser) {}

		@Override
		public void onAuthentication(UserDto aUser) {}

		@Override
		public void onStatusUpdate(UserDto aUser) {}

		@Override
		public void onLogout(UserDto aUser, boolean aExplicit) {}

	}

	private static final String STORAGE_ACCESS_TOKEN_KEY = "AuthenticationStatusProvider.accessToken";
	private static final String STORAGE_ACCESS_TOKEN_EXPIRATION_KEY = "AuthenticationStatusProvider.accessTokenExpiration";

	private static final String STORAGE_REFRESH_TOKEN_KEY = "AuthenticationStatusProvider.refreshToken";

	private static final int CHECK_EXTERNAL_STATUS_CHANGE_INTERVAL = 5 * 1000;
	private static final int CHECK_TOKEN_EXPIRATION_INTERVAL = 60 * 1000;
	private static final int REFRESH_TOKEN_BEFORE_EXPIRATION = 24 * 60 * 60 * 1000;

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	private final Storage storage;

	private final Timer checkExternalStatusChangeTimer;
	private final Timer checkTokenExpirationTimer;

	private final Set<Delegate> delegates = new TreeSet<>();

	private String lastAccessToken;

	private UserDto user;

	@Inject
	public AuthenticationManager(ApiService aApiService) {

		apiService = aApiService;

		storage = Storage.getLocalStorageIfSupported();

		checkExternalStatusChangeTimer = new Timer() {
			@Override
			public void run() {
				checkExternalStatusChange();
			}
		};
		checkTokenExpirationTimer = new Timer() {
			@Override
			public void run() {
				checkTokenExpiration();
			}
		};
	}

	public void addDelegate(Delegate aDelegate) {
		delegates.add(aDelegate);
	}

	public void removeDelegate(Delegate aDelegate) {
		delegates.remove(aDelegate);
	}

	public boolean isAuthenticated() {
		return getUser() != null;
	}

	public UserDto getUser() {
		return user;
	}

	public String getAccessToken() {
		return fetchValue(STORAGE_ACCESS_TOKEN_KEY);
	}

	private String getRefreshToken() {
		return fetchValue(STORAGE_REFRESH_TOKEN_KEY);
	}

	public void initialize(final OperationCallback<UserDto> aCallback) {

		log.info("Initializing...");

		if (getAccessToken() != null) {

			updateStatus(new OperationCallback<UserDto>() {
				@Override
				public void onSuccess(UserDto aUser) {

					aCallback.onSuccess(aUser);

					propagateInitialization(aUser);
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					aCallback.onError(aErrors);

					propagateInitialization(null);
				}
			});

			checkTokenExpiration();

		} else {

			log.info("User is not authenticated.");

			clearAuthentication(false);

			aCallback.onSuccess(null);

			propagateInitialization(null);
		}

		lastAccessToken = getAccessToken();

		checkExternalStatusChange();
	}

	public OperationRequest updateStatus(final OperationCallback<UserDto> aCallback) {

		log.info("Updating authentication status...");

		return new RequestAdapter(apiService.getCurrentUser(new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				setUser(aUser);

				log.info("User [" + aUser.getEmail() + "] is authenticated.");

				aCallback.onSuccess(aUser);

				propagateStatusUpdate(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				if (ErrorUtils.getErrorByCode(ErrorCode.CLIENT_EXCEPTION, aErrors) != null) {

					log.info("Could not update authentication status.");

					aCallback.onError(aErrors);

				} else if (ErrorUtils.getErrorByCode(ErrorCode.ACCESS_DENIED, aErrors) != null) {

					refreshToken(new OperationCallback<AuthenticationDto>() {
						@Override
						public void onSuccess(AuthenticationDto aAuthentication) {
							aCallback.onSuccess(aAuthentication.getUser());
						}

						@Override
						public void onError(List<ErrorDto> aErrors) {
							aCallback.onError(aErrors);
						}
					});

				} else {

					clearAuthentication(true);

					log.info("User is not authenticated.");

					aCallback.onError(aErrors);
				}
			}
		})));
	}

	public OperationRequest authenticate(final CredentialsDto aCredentials, final OperationCallback<UserDto> aCallback) {

		if (isAuthenticated()) {
			clearAuthentication(true);
		}

		log.info("Authenticating user [" + aCredentials.getEmail() + "]...");

		return new RequestAdapter(apiService.authenticate(aCredentials, new MethodCallbackAdapter<>(new OperationCallback<AuthenticationDto>() {
			@Override
			public void onSuccess(AuthenticationDto aAuthentication) {

				setAuthentication(aAuthentication);

				log.info("User [" + aAuthentication.getUser().getEmail() + "] has authenticated.");

				aCallback.onSuccess(aAuthentication.getUser());

				propagateAuthentication(aAuthentication.getUser());
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Authentication failed for user [" + aCredentials.getEmail() + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest logout(final OperationCallback<UserDto> aCallback) {

		UserDto user = getUser();

		if (user != null) {
			log.info("Logging out user [" + user.getEmail() + "]...");
		} else {
			log.info("Logging out...");
		}

		OperationRequest request = new RequestAdapter(apiService.logout(new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				log.info("User [" + aUser.getEmail() + "] has logged out.");

				aCallback.onSuccess(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Logging out failed.");

				aCallback.onError(aErrors);
			}
		})));

		clearAuthentication(true);

		return request;
	}

	private void refreshToken(final OperationCallback<AuthenticationDto> aCallback) {

		log.info("Refreshing access token...");

		if (getRefreshToken() != null) {
			apiService.refreshToken(getRefreshToken(), new MethodCallbackAdapter<>(new OperationCallback<AuthenticationDto>() {
				@Override
				public void onSuccess(AuthenticationDto aAuthentication) {

					setAuthentication(aAuthentication);

					log.info("Token for user [" + aAuthentication.getUser().getEmail() + "] has been refreshed.");

					aCallback.onSuccess(aAuthentication);
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					if (ErrorUtils.getErrorByCode(ErrorCode.CLIENT_EXCEPTION, aErrors) != null) {
						log.severe("Token refresh failed");
					} else {

						clearAuthentication(false);

						log.info("Refresh token is invalid.");
					}

					aCallback.onError(aErrors);
				}
			}));
		} else {

			clearAuthentication(false);

			log.severe("No refresh token.");

			aCallback.onError(Arrays.asList(new ErrorDto(ErrorCode.ACCESS_DENIED, "Access denied.")));
		}
	}

	private void checkExternalStatusChange() {

		String token = getAccessToken();

		if (!ObjectUtils.nullSafeEquals(token, lastAccessToken)) {
			Window.Location.reload();
		}

		lastAccessToken = token;

		checkExternalStatusChangeTimer.schedule(CHECK_EXTERNAL_STATUS_CHANGE_INTERVAL);
	}

	private void checkTokenExpiration() {

		boolean scheduleChecking = true;

		if (getAccessTokenExpiration() != null) {
			if (getAccessTokenExpiration().getTime() - new Date().getTime() <= REFRESH_TOKEN_BEFORE_EXPIRATION) {

				scheduleChecking = false;

				refreshToken(new NoOpOperationCallback<AuthenticationDto>() {
					@Override
					public void onFinish(boolean aSuccess, AuthenticationDto aData, List<ErrorDto> aErrors) {
						checkTokenExpirationTimer.schedule(CHECK_TOKEN_EXPIRATION_INTERVAL);
					}
				});
			}
		}

		if (scheduleChecking) {
			checkTokenExpirationTimer.schedule(CHECK_TOKEN_EXPIRATION_INTERVAL);
		}
	}

	private void propagateInitialization(UserDto aUser) {
		for (Delegate delegate : new ArrayList<>(delegates)) {
			delegate.onInitialization(aUser);
		}
	}

	private void propagateAuthentication(UserDto aUser) {
		for (Delegate delegate : new ArrayList<>(delegates)) {
			delegate.onAuthentication(aUser);
		}
	}

	private void propagateStatusUpdate(UserDto aUser) {
		for (Delegate delegate : new ArrayList<>(delegates)) {
			delegate.onStatusUpdate(aUser);
		}
	}

	private void propagateLogout(UserDto aUser, boolean aExplicit) {
		for (Delegate delegate : new ArrayList<>(delegates)) {
			delegate.onLogout(aUser, aExplicit);
		}
	}

	private void setAuthentication(AuthenticationDto aAuthentication) {

		setAccessToken(aAuthentication.getAccessToken());
		setAccessTokenExpiration(aAuthentication.getAccessTokenExpiration());

		setRefreshToken(aAuthentication.getAccessToken());

		setUser(aAuthentication.getUser());
	}

	private void clearAuthentication(boolean aPropagateLogout) {

		UserDto oldUser = getUser();

		setAccessToken(null);
		setAccessTokenExpiration(null);
		setRefreshToken(null);
		setUser(null);

		if (aPropagateLogout) {
			propagateLogout(oldUser, false);
		}
	}

	private void setAccessToken(String aAccessToken) {

		storeValue(STORAGE_ACCESS_TOKEN_KEY, aAccessToken);

		lastAccessToken = aAccessToken;
	}

	private Date getAccessTokenExpiration() {

		String value = fetchValue(STORAGE_ACCESS_TOKEN_EXPIRATION_KEY);

		return value != null ? new Date(Long.valueOf(value)) : null;
	}

	private void setAccessTokenExpiration(Date aAccessTokenExpiration) {
		storeValue(STORAGE_ACCESS_TOKEN_EXPIRATION_KEY, aAccessTokenExpiration != null ? String.valueOf(aAccessTokenExpiration.getTime()) : null);
	}

	private void setRefreshToken(String aRefreshToken) {
		storeValue(STORAGE_REFRESH_TOKEN_KEY, aRefreshToken);
	}

	private void setUser(UserDto aUser) {
		user = aUser;
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
