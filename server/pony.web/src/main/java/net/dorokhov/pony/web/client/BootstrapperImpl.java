package net.dorokhov.pony.web.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.AuthenticationDispatcherFilter;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.UserDto;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

public class BootstrapperImpl extends DefaultBootstrapper implements AuthenticationManager.InitializationHandler {

	private final AuthenticationDispatcherFilter authenticationDispatcherFilter;

	private final AuthenticationManager authenticationManager;

	@Inject
	public BootstrapperImpl(PlaceManager aPlaceManager,
							AuthenticationDispatcherFilter aAuthenticationDispatcherFilter,
							ApiService aApiService,
							AuthenticationManager aAuthenticationManager) {

		super(aPlaceManager);

		authenticationDispatcherFilter = aAuthenticationDispatcherFilter;
		authenticationManager = aAuthenticationManager;
	}

	@Override
	public void onBootstrap() {

		Defaults.setServiceRoot("/api");

		FilterawareDispatcher dispatcher = DefaultFilterawareDispatcher.singleton();

		dispatcher.addFilter(authenticationDispatcherFilter);

		Defaults.setDispatcher(dispatcher);
		Defaults.setDateFormat(null);

		authenticationManager.initialize(this);
	}

	@Override
	public void onInitialization(UserDto aUser) {
		super.onBootstrap();
	}

}
