package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.shared.UserDto;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;

public class ToolbarView extends ViewWithUiHandlers<ToolbarUiHandlers> implements ToolbarPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ToolbarView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private UserDto currentUser;

	@UiField
	Button currentUserButton;

	@UiField
	AnchorListItem editProfileButton;

	@UiField
	AnchorListItem logoutButton;

	public ToolbarView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public UserDto getCurrentUser() {
		return currentUser;
	}

	@Override
	public void setCurrentUser(UserDto aCurrentUser) {

		currentUser = aCurrentUser;

		updateUser();
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
		currentUserButton.setText(currentUser != null ? currentUser.getName() : null);
	}

}
