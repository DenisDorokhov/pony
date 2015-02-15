package net.dorokhov.pony.web.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.client.service.AuthenticationDispatcherFilter;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.client.service.common.NoOpOperationCallback;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

import javax.inject.Inject;
import java.util.List;

public class BootstrapperImpl extends DefaultBootstrapper {

	private static final String LOADING_CONTAINER_ID = "loadingContainer";

	private final AuthenticationDispatcherFilter authenticationDispatcherFilter;

	private final AuthenticationManager authenticationManager;

	@Inject
	public BootstrapperImpl(PlaceManager aPlaceManager,
							AuthenticationDispatcherFilter aAuthenticationDispatcherFilter,
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

		authenticationManager.initialize(new NoOpOperationCallback<UserDto>() {
			@Override
			public void onFinish(boolean aSuccess, UserDto aData, List<ErrorDto> aErrors) {
				doBootstrap();
			}
		});
	}

	private void doBootstrap() {

		Element loadingContainer = Element.as(DOM.getElementById(LOADING_CONTAINER_ID));

		if (loadingContainer != null) {
			loadingContainer.removeFromParent();
		}

		super.onBootstrap();
	}

}
