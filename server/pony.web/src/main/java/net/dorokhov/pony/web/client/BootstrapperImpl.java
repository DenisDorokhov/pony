package net.dorokhov.pony.web.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.client.service.ApiService;
import net.dorokhov.pony.web.client.service.AuthenticationDispatcherFilter;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.ResponseDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

import java.util.logging.Logger;

public class BootstrapperImpl extends DefaultBootstrapper {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final AuthenticationDispatcherFilter authenticationDispatcherFilter;

	private final ApiService apiService;

	private final AuthenticationManager authenticationManager;

	@Inject
	public BootstrapperImpl(PlaceManager aPlaceManager,
							AuthenticationDispatcherFilter aAuthenticationDispatcherFilter,
							ApiService aApiService,
							AuthenticationManager aAuthenticationManager) {

		super(aPlaceManager);

		authenticationDispatcherFilter = aAuthenticationDispatcherFilter;
		apiService = aApiService;
		authenticationManager = aAuthenticationManager;
	}

	@Override
	public void onBootstrap() {

		Defaults.setServiceRoot("/api");

		FilterawareDispatcher dispatcher = DefaultFilterawareDispatcher.singleton();

		dispatcher.addFilter(authenticationDispatcherFilter);

		Defaults.setDispatcher(dispatcher);

		if (authenticationManager.isAuthenticated()) {
			updateCurrentUser();
		} else {
			super.onBootstrap();
		}
	}

	private void updateCurrentUser() {
		apiService.getCurrentUser(new MethodCallback<ResponseDto<UserDto>>() {
			@Override
			public void onFailure(Method aMethod, Throwable aThrowable) {

				log.severe("Could not get current user because of unexpected error: " + aThrowable.getMessage());

				authenticationManager.clearAuthentication();

				BootstrapperImpl.super.onBootstrap();
			}

			@Override
			public void onSuccess(Method aMethod, ResponseDto<UserDto> aResponse) {

				if (authenticationManager.isAuthenticated() && aResponse.isSuccessful()) {

					log.info("Current user is [" + aResponse.getData().getEmail() + "]");

					authenticationManager.setCurrentUser(aResponse.getData());

				}else{

					log.warning("Could not get current user.");

					authenticationManager.clearAuthentication();
				}

				BootstrapperImpl.super.onBootstrap();
			}
		});
	}

}
