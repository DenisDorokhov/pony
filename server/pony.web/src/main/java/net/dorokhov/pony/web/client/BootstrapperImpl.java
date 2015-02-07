package net.dorokhov.pony.web.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.DefaultBootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.fusesource.restygwt.client.Defaults;

public class BootstrapperImpl extends DefaultBootstrapper {

	@Inject
	public BootstrapperImpl(PlaceManager placeManager) {
		super(placeManager);
	}

	@Override
	public void onBootstrap() {

		Defaults.setServiceRoot("/api");

		super.onBootstrap();
	}

}
