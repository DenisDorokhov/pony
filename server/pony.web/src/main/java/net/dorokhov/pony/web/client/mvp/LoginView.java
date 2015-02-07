package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.shared.CredentialsDto;
import org.gwtbootstrap3.client.ui.Input;

public class LoginView extends ViewWithUiHandlers<LoginUiHandlers> implements LoginPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LoginView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Input emailField;

	@UiField
	Input passwordField;

	public LoginView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("loginButton")
	void onSaveClick(ClickEvent aEvent) {
		requestLogin();
	}

	@UiHandler(value = {"emailField", "passwordField"})
	void onKeyUp(KeyUpEvent aEvent) {
		if (aEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			requestLogin();
		}
	}

	private void requestLogin() {

		CredentialsDto credentials = new CredentialsDto();

		credentials.setEmail(emailField.getText());
		credentials.setPassword(passwordField.getText());

		getUiHandlers().onLoginRequested(credentials);
	}

}
