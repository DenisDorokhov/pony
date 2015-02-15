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
import net.dorokhov.pony.web.client.control.ErrorAwareForm;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Input;

import java.util.ArrayList;
import java.util.List;

public class LoginView extends ViewWithUiHandlers<LoginUiHandlers> implements LoginPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LoginView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private boolean loading;

	private List<ErrorDto> errors;

	@UiField
	ErrorAwareForm form;

	@UiField
	FieldSet fieldSet;

	@UiField
	Input emailField;

	@UiField
	Input passwordField;

	public LoginView() {

		initWidget(uiBinder.createAndBindUi(this));

		updateErrors();
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean aLoading) {

		loading = aLoading;

		updateLoading();
	}

	@Override
	public List<ErrorDto> getErrors() {

		if (errors == null) {
			errors = new ArrayList<>();
		}

		return errors;
	}

	@Override
	public void setErrors(List<ErrorDto> aErrors) {

		errors = aErrors;

		updateErrors();
	}

	@Override
	public void clearForm() {
		emailField.setText("");
		passwordField.setText("");
	}

	@Override
	public void clearErrors() {

		getErrors().clear();

		updateErrors();
	}

	@Override
	public void setFocus() {
		emailField.setFocus(true);
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

	private void updateLoading() {
		fieldSet.setEnabled(!isLoading());
	}

	private void updateErrors() {
		form.setErrors(getErrors());
	}

	private void requestLogin() {

		CredentialsDto credentials = new CredentialsDto();

		credentials.setEmail(emailField.getText());
		credentials.setPassword(passwordField.getText());

		getUiHandlers().onLoginRequested(credentials);
	}

}
