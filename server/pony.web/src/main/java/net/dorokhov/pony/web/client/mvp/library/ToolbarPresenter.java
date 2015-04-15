package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.EmptyLibraryEvent;
import net.dorokhov.pony.web.client.event.RefreshRequestEvent;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.client.service.BusyModeManager;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.LibraryScanner;
import net.dorokhov.pony.web.client.service.common.NoOpOperationCallback;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;
import java.util.List;

public class ToolbarPresenter extends PresenterWidget<ToolbarPresenter.MyView> implements ToolbarUiHandlers, BusyModeManager.Delegate, LibraryScanner.Delegate, AuthenticationManager.Delegate, EmptyLibraryEvent.Handler {

	public interface MyView extends View, HasUiHandlers<ToolbarUiHandlers> {

		public UserDto getUser();

		public void setUser(UserDto aCurrentUser);

		public boolean isRefreshing();

		public void setRefreshing(boolean aRefreshing);

		public boolean isScanning();

		public void setScanning(boolean aScanning);

	}

	private final ScanningPresenter scanningPresenter;
	private final LogPresenter logPresenter;
	private final UserListPresenter userListPresenter;
	private final SettingsPresenter settingsPresenter;

	private final BusyModeManager busyModeManager;

	private final LibraryScanner libraryScanner;

	private final AuthenticationManager authenticationManager;

	private final ErrorNotifier errorNotifier;

	private boolean didShowScanningWithEmptyLibrary = false;

	@Inject
	public ToolbarPresenter(EventBus aEventBus, MyView aView,
							ScanningPresenter aScanningPresenter, LogPresenter aLogPresenter, UserListPresenter aUserListPresenter, SettingsPresenter aSettingsPresenter,
							BusyModeManager aBusyModeManager, LibraryScanner aLibraryScanner,
							AuthenticationManager aAuthenticationManager, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		scanningPresenter = aScanningPresenter;
		logPresenter = aLogPresenter;
		userListPresenter = aUserListPresenter;
		settingsPresenter = aSettingsPresenter;

		busyModeManager = aBusyModeManager;
		libraryScanner = aLibraryScanner;
		authenticationManager = aAuthenticationManager;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(EmptyLibraryEvent.TYPE, this);

		busyModeManager.addDelegate(this);
		libraryScanner.addDelegate(this);
		authenticationManager.addDelegate(this);
	}

	@Override
	protected void onUnbind() {

		busyModeManager.removeDelegate(this);
		libraryScanner.removeDelegate(this);
		authenticationManager.removeDelegate(this);

		super.onUnbind();
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		didShowScanningWithEmptyLibrary = false;

		getView().setUser(authenticationManager.getUser());
	}

	@Override
	public void onBusyModeStarted() {
		getView().setRefreshing(true);
	}

	@Override
	public void onBusyModeEnded() {
		getView().setRefreshing(false);
	}

	@Override
	public void onScanStarted(LibraryScanner aLibraryScanner) {
		getView().setScanning(true);
	}

	@Override
	public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus) {}

	@Override
	public void onScanFinished(LibraryScanner aLibraryScanner) {
		getView().setScanning(false);
	}

	@Override
	public void onInitialization(UserDto aUser) {
		getView().setUser(aUser);
	}

	@Override
	public void onAuthentication(UserDto aUser) {
		getView().setUser(aUser);
	}

	@Override
	public void onStatusUpdate(UserDto aUser) {
		getView().setUser(aUser);
	}

	@Override
	public void onLogout(UserDto aUser, boolean aExplicit) {
		getView().setUser(null);
	}

	@Override
	public void onRefreshRequested() {
		getEventBus().fireEvent(new RefreshRequestEvent());
	}
	
	@Override
	public void onSettingsRequested() {
		addToPopupSlot(settingsPresenter);
	}

	@Override
	public void onScanningRequested() {
		addToPopupSlot(scanningPresenter);
	}

	@Override
	public void onLogRequested() {
		addToPopupSlot(logPresenter);
	}

	@Override
	public void onUsersRequested() {
		addToPopupSlot(userListPresenter);
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

	@Override
	public void onEmptyLibrary(EmptyLibraryEvent aEvent) {
		if (!didShowScanningWithEmptyLibrary) {

			addToPopupSlot(scanningPresenter);

			didShowScanningWithEmptyLibrary = true;
		}
	}

}
