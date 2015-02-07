package net.dorokhov.pony.web.client.gin;

import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModuleCustom;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.mvp.*;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		install(new DefaultModule());

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class, LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(LibraryPresenter.class, LibraryPresenter.MyView.class, LibraryView.class, LibraryPresenter.MyProxy.class);

		bindSingletonPresenterWidget(PlayerPresenter.class, PlayerPresenter.MyView.class, PlayerView.class);
		bindSingletonPresenterWidget(LibraryContentPresenter.class, LibraryContentPresenter.MyView.class, LibraryContentView.class);

		bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class, ErrorView.class, ErrorPresenter.MyProxy.class);

		bindConstant().annotatedWith(DefaultPlace.class).to(PlaceTokens.LIBRARY);
		bindConstant().annotatedWith(ErrorPlace.class).to(PlaceTokens.ERROR);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(PlaceTokens.LOGIN);

		RestDispatchAsyncModuleCustom.Builder dispatchBuilder = new RestDispatchAsyncModuleCustom.Builder();

		install(dispatchBuilder.build());

		bindConstant().annotatedWith(RestApplicationPath.class).to("/api");
	}

}
