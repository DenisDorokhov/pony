package net.dorokhov.pony.web.client;

import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.proxy.DefaultPlaceManager;
import net.dorokhov.pony.web.client.mvp.ApplicationPresenter;
import net.dorokhov.pony.web.client.mvp.ApplicationView;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		install(new DefaultModule());

		bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class, ApplicationPresenter.MyProxy.class);

		bindConstant().annotatedWith(DefaultPlace.class).to(PlaceTokens.HOME);
		bindConstant().annotatedWith(ErrorPlace.class).to(PlaceTokens.HOME);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(PlaceTokens.HOME);
	}

}
