package net.dorokhov.pony.web.client.mvp;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.ApiService;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.logging.Logger;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.LOGIN)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {}

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy,
						  ApiService aApiService) {

		super(eventBus, view, proxy, RevealType.RootLayout);

		apiService = aApiService;

		getView().setUiHandlers(this);
	}

	@Override
	public void onLoginRequested(CredentialsDto aCredentials) {

		log.fine("Authenticating...");

		apiService.authenticate(aCredentials, new MethodCallback<ResponseDto<String>>() {
			@Override
			public void onFailure(Method aMethod, Throwable aThrowable) {
				log.severe("Authentication failed.");
			}

			@Override
			public void onSuccess(Method aMethod, ResponseDto<String> aResponse) {

				String token = aResponse.getData();

				log.fine("Authentication [" + token + "] successful.");
			}
		});
	}

}
