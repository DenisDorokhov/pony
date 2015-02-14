package net.dorokhov.pony.web.client.mvp;

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
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.common.OperationCallback;
import net.dorokhov.pony.web.client.common.OperationRequest;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;

import java.util.List;

public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> implements LoginUiHandlers {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.LOGIN)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {}

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {

		public List<ErrorDto> getErrors();

		public void setErrors(List<ErrorDto> aErrors);

		public void clearForm();
		public void clearErrors();

		public void setFocus();

	}

	private final PlaceManager placeManager;

	private final AuthenticationManager authenticationManager;

	private OperationRequest currentRequest;

	@Inject
	public LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager aPlaceManager,
						  AuthenticationManager aAuthenticationManager) {

		super(eventBus, view, proxy, RevealType.Root);

		placeManager = aPlaceManager;
		authenticationManager = aAuthenticationManager;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		getView().setFocus();
	}

	@Override
	public void onLoginRequested(CredentialsDto aCredentials) {

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		currentRequest = authenticationManager.authenticate(aCredentials, new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				getView().clearForm();
				getView().clearErrors();

				PlaceRequest.Builder requestBuilder = new PlaceRequest.Builder().nameToken(PlaceTokens.LIBRARY);

				placeManager.revealPlace(requestBuilder.build());

				currentRequest = null;
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				getView().setErrors(aErrors);

				currentRequest = null;
			}
		});
	}

}