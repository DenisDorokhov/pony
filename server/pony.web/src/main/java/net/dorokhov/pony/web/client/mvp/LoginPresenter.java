package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import net.dorokhov.pony.web.client.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.ApiService;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.AuthenticationDto;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.List;
import java.util.logging.Logger;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.LOGIN)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {}

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {

		public List<ErrorDto> getErrors();

		public void setErrors(List<ErrorDto> aErrors);

		public void clearErrors();

		public void setFocus();

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final PlaceManager placeManager;

	private final ApiService apiService;

	private final AuthenticationManager authenticationManager;

	private final Messages messages;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager aPlaceManager,
						  ApiService aApiService,
						  AuthenticationManager aAuthenticationManager,
						  Messages aMessages) {

		super(eventBus, view, proxy, RevealType.Root);

		placeManager = aPlaceManager;
		apiService = aApiService;
		authenticationManager = aAuthenticationManager;
		messages = aMessages;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		getView().setFocus();
	}

	@Override
	public void onLoginRequested(CredentialsDto aCredentials) {

		log.info("Authenticating...");

		apiService.authenticate(aCredentials, new MethodCallback<ResponseDto<AuthenticationDto>>() {
			@Override
			public void onFailure(Method aMethod, Throwable aThrowable) {

				log.severe("Authentication failed because of unexpected error: " + aThrowable.getMessage());

				Window.alert(messages.loginAlertUnexpectedError());
			}

			@Override
			public void onSuccess(Method aMethod, ResponseDto<AuthenticationDto> aResponse) {

				if (aResponse.isSuccessful()) {
					authenticationManager.authenticate(aResponse.getData());
				} else {
					authenticationManager.clearAuthentication();
				}

				if (authenticationManager.isAuthenticated()) {

					getView().clearErrors();

					log.info("Authentication [" + authenticationManager.getCurrentUser().getEmail() + "] successful.");

					PlaceRequest.Builder requestBuilder = new PlaceRequest.Builder().nameToken(PlaceTokens.LIBRARY);

					placeManager.revealPlace(requestBuilder.build());

				} else {

					getView().setErrors(aResponse.getErrors());

					log.severe("Authentication failed.");
				}
			}
		});
	}

}
