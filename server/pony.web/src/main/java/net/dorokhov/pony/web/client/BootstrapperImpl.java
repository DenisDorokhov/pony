package net.dorokhov.pony.web.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.client.service.AuthenticationDispatcherFilter;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

public class BootstrapperImpl extends DefaultBootstrapper {

	private final AuthenticationDispatcherFilter authenticationDispatcherFilter;

	@Inject
	public BootstrapperImpl(PlaceManager aPlaceManager, AuthenticationDispatcherFilter aAuthenticationDispatcherFilter) {

		super(aPlaceManager);

		authenticationDispatcherFilter = aAuthenticationDispatcherFilter;
	}

	@Override
	public void onBootstrap() {

		Defaults.setServiceRoot("/api");

		FilterawareDispatcher dispatcher = DefaultFilterawareDispatcher.singleton();

		dispatcher.addFilter(authenticationDispatcherFilter);

		Defaults.setDispatcher(dispatcher);

		super.onBootstrap();
	}

}
