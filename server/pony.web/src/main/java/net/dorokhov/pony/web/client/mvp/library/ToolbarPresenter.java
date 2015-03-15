package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.RefreshRequestEvent;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.common.NoOpOperationCallback;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class ToolbarPresenter extends PresenterWidget<ToolbarPresenter.MyView> implements ToolbarUiHandlers {

	public interface MyView extends View, HasUiHandlers<ToolbarUiHandlers> {

		public UserDto getUser();

		public void setUser(UserDto aCurrentUser);

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ScanningPresenter scanningPresenter;

	private final AuthenticationManager authenticationManager;

	private final ErrorNotifier errorNotifier;

	@Inject
	public ToolbarPresenter(EventBus aEventBus, MyView aView,
							ScanningPresenter aScanningPresenter,
							AuthenticationManager aAuthenticationManager, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		scanningPresenter = aScanningPresenter;

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
	public void onRefreshRequested() {
		getEventBus().fireEvent(new RefreshRequestEvent());
	}
	
	@Override
	public void onSettingsRequested() {
		log.info("Settings requested.");
	}

	@Override
	public void onScanningRequested() {
		addToPopupSlot(scanningPresenter);
	}

	@Override
	public void onLogRequested() {

	}

	@Override
	public void onUsersRequested() {

	}

	@Override
	public void onEditProfileRequested() {
		log.info("Profile edit requested.");
	}

	@Override
	public void onLogoutRequested() {
		authenticationManager.logout(new NoOpOperationCallback<UserDto>() {
			@Override
			public void onError(List<ErrorDto> aErrors) {
				errorNotifier.notifyOfErrors(aErrors);
			}
		});
	}

}
