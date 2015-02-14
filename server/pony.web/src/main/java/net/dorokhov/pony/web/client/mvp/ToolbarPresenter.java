package net.dorokhov.pony.web.client.mvp;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;

import java.util.List;
import java.util.logging.Logger;

public class ToolbarPresenter extends PresenterWidget<ToolbarPresenter.MyView> implements ToolbarUiHandlers {

	public interface MyView extends View, HasUiHandlers<ToolbarUiHandlers> {

		public UserDto getUser();

		public void setUser(UserDto aCurrentUser);

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final PlaceManager placeManager;

	private final AuthenticationManager authenticationManager;

	private final ErrorNotifier errorNotifier;

	private OperationRequest currentRequest;

	@Inject
	public ToolbarPresenter(EventBus aEventBus, MyView aView, PlaceManager aPlaceManager,
							AuthenticationManager aAuthenticationManager, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		placeManager = aPlaceManager;
		authenticationManager = aAuthenticationManager;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		getView().setUser(authenticationManager.getUser());
	}

	@Override
	public void onEditProfileRequested() {
		log.info("Profile edit requested.");
	}

	@Override
	public void onLogoutRequested() {

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		currentRequest = authenticationManager.logout(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {
				currentRequest = null;
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				currentRequest = null;

				errorNotifier.notifyOfErrors(aErrors);
			}
		});

		PlaceRequest.Builder requestBuilder = new PlaceRequest.Builder().nameToken(PlaceTokens.LOGIN);

		placeManager.revealPlace(requestBuilder.build());
	}

}
