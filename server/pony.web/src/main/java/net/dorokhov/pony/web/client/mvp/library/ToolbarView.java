package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.shared.RoleDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;

public class ToolbarView extends ViewWithUiHandlers<ToolbarUiHandlers> implements ToolbarPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ToolbarView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Button refreshButton;

	@UiField
	Button systemButton;

	@UiField
	Button currentUserButton;

	@UiField
	AnchorListItem editProfileButton;

	@UiField
	AnchorListItem logoutButton;

	private UserDto currentUser;

	private boolean refreshing;

	private boolean scanning;

	public ToolbarView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public UserDto getUser() {
		return currentUser;
	}

	@Override
	public void setUser(UserDto aCurrentUser) {

		currentUser = aCurrentUser;

		updateUser();
	}

	@Override
	public boolean isRefreshing() {
		return refreshing;
	}

	@Override
	public void setRefreshing(boolean aRefreshing) {

		refreshing = aRefreshing;

		// TODO: animate refresh button (impossible now because of browser rendering bugs related to CSS columns)
	}

	@Override
	public boolean isScanning() {
		return scanning;
	}

	@Override
	public void setScanning(boolean aScanning) {

		scanning = aScanning;

		// TODO: animate system button (impossible now because of browser rendering bugs related to CSS columns)
	}

	@UiHandler("refreshButton")
	void onRefreshButtonClick(ClickEvent aEvent) {
		getUiHandlers().onRefreshRequested();
	}

	@UiHandler("settingsButton")
	void onSettingsButtonClick(ClickEvent aEvent) {
		getUiHandlers().onSettingsRequested();
	}

	@UiHandler("scanningButton")
	void onScanningButtonClick(ClickEvent aEvent) {
		getUiHandlers().onScanningRequested();
	}

	@UiHandler("logButton")
	void onLogButtonClick(ClickEvent aEvent) {
		getUiHandlers().onLogRequested();
	}

	@UiHandler("usersButton")
	void onUsersButtonClick(ClickEvent aEvent) {
		getUiHandlers().onUsersRequested();
	}

	@UiHandler("editProfileButton")
	void onEditProfileButtonClick(ClickEvent aEvent) {
		getUiHandlers().onEditProfileRequested();
	}

	@UiHandler("logoutButton")
	void onLogoutButtonClick(ClickEvent aEvent) {
		getUiHandlers().onLogoutRequested();
	}

	private void updateUser() {
		systemButton.setVisible(currentUser != null && currentUser.getRole() == RoleDto.ADMIN);
		currentUserButton.setText(currentUser != null ? currentUser.getName() : null);
	}

}
