package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.Cookies;
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

	private static final int CHECK_EXTERNAL_STATUS_CHANGE_INTERVAL = 5 * 1000;
	private static final int CHECK_TOKEN_EXPIRATION_INTERVAL = 5 * 60 * 1000;
	private static final int CHECK_STATUS_INTERVAL = 60 * 1000;
	private static final int REFRESH_TOKEN_BEFORE_EXPIRATION = 24 * 60 * 60 * 1000;

	private static final String COOKIE_DOWNLOAD_ACCESS_TOKEN = "Download-Access-Token";

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	private final SecurityStorage securityStorage;

	private final Timer checkExternalStatusChangeTimer;
	private final Timer checkTokenExpirationTimer;
	private final Timer checkStatusTimer;

	private final Set<Delegate> delegates = new TreeSet<>();

	private String lastAccessToken;

	private UserDto user;

	@Inject
	public AuthenticationManager(ApiService aApiService, SecurityStorage aSecurityStorage) {

		apiService = aApiService;
		securityStorage = aSecurityStorage;

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
		checkStatusTimer = new Timer() {
			@Override
			public void run() {
				checkStatus();
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

	public void initialize(final OperationCallback<UserDto> aCallback) {

		log.info("Initializing...");

		setDownloadAccessToken(securityStorage.getAccessToken());

		if (securityStorage.getAccessToken() != null) {

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

		lastAccessToken = securityStorage.getAccessToken();

		checkExternalStatusChangeTimer.schedule(CHECK_EXTERNAL_STATUS_CHANGE_INTERVAL);
		checkStatusTimer.schedule(CHECK_STATUS_INTERVAL);
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

				if (ErrorUtils.getErrorByCode(aErrors, ErrorCodes.CLIENT_REQUEST_FAILED, ErrorCodes.CLIENT_OFFLINE) != null) {

					log.info("Could not update authentication status.");

					aCallback.onError(aErrors);

				} else if (ErrorUtils.getErrorByCode(aErrors, ErrorCodes.ACCESS_DENIED) != null) {

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

		if (securityStorage.getRefreshToken() != null) {
			apiService.refreshToken(securityStorage.getRefreshToken(), new MethodCallbackAdapter<>(new OperationCallback<AuthenticationDto>() {
				@Override
				public void onSuccess(AuthenticationDto aAuthentication) {

					setAuthentication(aAuthentication);

					log.info("Token for user [" + aAuthentication.getUser().getEmail() + "] has been refreshed.");

					aCallback.onSuccess(aAuthentication);
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					if (ErrorUtils.getErrorByCode(aErrors, ErrorCodes.CLIENT_REQUEST_FAILED, ErrorCodes.CLIENT_OFFLINE) != null) {
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

			aCallback.onError(Arrays.asList(new ErrorDto(ErrorCodes.ACCESS_DENIED, "Access denied.")));
		}
	}

	private void checkExternalStatusChange() {

		String token = securityStorage.getAccessToken();

		if (!ObjectUtils.nullSafeEquals(token, lastAccessToken)) {
			Window.Location.reload();
		}

		lastAccessToken = token;

		checkExternalStatusChangeTimer.schedule(CHECK_EXTERNAL_STATUS_CHANGE_INTERVAL);
	}

	private void checkTokenExpiration() {

		boolean scheduleChecking = true;

		if (securityStorage.getAccessTokenExpiration() != null) {
			if (securityStorage.getAccessTokenExpiration().getTime() - new Date().getTime() <= REFRESH_TOKEN_BEFORE_EXPIRATION) {

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
	
	private void checkStatus() {
		
		if (isAuthenticated()) {
			updateStatus(new NoOpOperationCallback<UserDto>());
		}
		
		checkStatusTimer.schedule(CHECK_STATUS_INTERVAL);
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
		setUser(aAuthentication.getUser());

		securityStorage.setAccessTokenExpiration(aAuthentication.getAccessTokenExpiration());
		securityStorage.setRefreshToken(aAuthentication.getAccessToken());
	}

	private void clearAuthentication(boolean aPropagateLogout) {

		UserDto oldUser = getUser();

		setAccessToken(null);
		setUser(null);

		securityStorage.setAccessTokenExpiration(null);
		securityStorage.setRefreshToken(null);

		if (aPropagateLogout) {
			propagateLogout(oldUser, false);
		}
	}

	private void setAccessToken(String aAccessToken) {

		securityStorage.setAccessToken(aAccessToken);

		setDownloadAccessToken(aAccessToken);

		lastAccessToken = aAccessToken;
	}

	private void setUser(UserDto aUser) {
		user = aUser;
	}

	private void setDownloadAccessToken(String aAccessToken) {
		if (aAccessToken != null) {
			Cookies.setCookie(COOKIE_DOWNLOAD_ACCESS_TOKEN, aAccessToken);
		} else {
			Cookies.removeCookie(COOKIE_DOWNLOAD_ACCESS_TOKEN);
		}
	}

}
