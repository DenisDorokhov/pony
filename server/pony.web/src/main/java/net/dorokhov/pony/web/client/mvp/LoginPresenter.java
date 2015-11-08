package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;
import java.util.List;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers, AuthenticationManager.Delegate {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.LOGIN)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {}

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers>, HasEnabled {

		public List<ErrorDto> getErrors();

		public void setErrors(List<ErrorDto> aErrors);

		public void clearForm();

		public void setFocus();

	}

	private final AuthenticationManager authenticationManager;

	private OperationRequest currentRequest;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy,
						  AuthenticationManager aAuthenticationManager) {

		super(eventBus, view, proxy, RevealType.Root);

		authenticationManager = aAuthenticationManager;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		Window.setTitle(Messages.INSTANCE.loginTitle());

		getView().clearForm();
		getView().setFocus();
	}

	@Override
	protected void onBind() {

		super.onBind();

		authenticationManager.addDelegate(this);
	}

	@Override
	protected void onUnbind() {

		authenticationManager.removeDelegate(this);

		super.onUnbind();
	}

	@Override
	public void onLoginRequested(CredentialsDto aCredentials) {

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		getView().setEnabled(false);

		currentRequest = authenticationManager.authenticate(aCredentials, new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				getView().setErrors(null);

				currentRequest = null;
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				getView().setEnabled(true);
				getView().setErrors(aErrors);

				getView().setFocus();

				currentRequest = null;
			}
		});
	}

	@Override
	public void onInitialization(UserDto aUser) {}

	@Override
	public void onAuthentication(UserDto aUser) {
		getView().setEnabled(false);
	}

	@Override
	public void onStatusUpdate(UserDto aUser) {}

	@Override
	public void onLogout(UserDto aUser) {
		getView().setEnabled(true);
	}

}
