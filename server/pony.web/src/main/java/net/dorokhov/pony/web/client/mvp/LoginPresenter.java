package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.AuthenticationResource;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ResponseDto;

import java.util.logging.Logger;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.LOGIN)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {}

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final RestDispatch dispatcher;

	private final AuthenticationResource authenticationResource;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy,
						  RestDispatch aDispatcher, AuthenticationResource aAuthenticationResource) {

		super(eventBus, view, proxy, RevealType.RootLayout);

		dispatcher = aDispatcher;
		authenticationResource = aAuthenticationResource;

		getView().setUiHandlers(this);
	}

	@Override
	public void onLoginRequested(CredentialsDto aCredentials) {

		log.fine("Authenticating...");

		dispatcher.execute(authenticationResource.authenticate(aCredentials), new AsyncCallback<ResponseDto<String>>() {

			@Override
			public void onFailure(Throwable aCaught) {
				log.severe("Authentication failed.");
			}

			@Override
			public void onSuccess(ResponseDto<String> aResult) {

				String token = aResult.getData();

				log.fine("Authentication [" + token + "] successful.");
			}
		});
	}

}
