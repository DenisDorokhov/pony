package net.dorokhov.pony.web.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.client.common.OperationCallback;
import net.dorokhov.pony.web.client.service.ApiService;
import net.dorokhov.pony.web.client.service.AuthenticationDispatcherFilter;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.FilterawareDispatcher;

import java.util.List;

public class BootstrapperImpl extends DefaultBootstrapper {

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

		authenticationManager.updateStatus(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aData) {
				proceed();
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {
				proceed();
			}
		});
	}

	private void proceed() {
		super.onBootstrap();
	}

}
