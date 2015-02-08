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
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.ApiService;
import net.dorokhov.pony.web.client.common.AuthenticationStatus;
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

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	private final AuthenticationStatus authenticationStatus;

	private final Messages messages;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy,
						  ApiService aApiService,
						  AuthenticationStatus aAuthenticationStatus,
						  Messages aMessages) {

		super(eventBus, view, proxy, RevealType.RootLayout);

		apiService = aApiService;
		authenticationStatus = aAuthenticationStatus;
		messages = aMessages;

		getView().setUiHandlers(this);
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
					authenticationStatus.updateAuthentication(aResponse.getData());
				} else {
					authenticationStatus.clearAuthentication();
				}

				if (authenticationStatus.isAuthenticated()) {

					getView().clearErrors();

					log.info("Authentication [" + authenticationStatus.getCurrentUser().getEmail() + "] successful.");

				} else {

					getView().setErrors(aResponse.getErrors());

					log.severe("Authentication failed.");
				}
			}
		});
	}

}
