package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.ErrorAwareForm;
import net.dorokhov.pony.web.client.control.status.ErrorIndicator;
import net.dorokhov.pony.web.client.control.status.LoadingIndicator;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.RoleDto;
import net.dorokhov.pony.web.shared.UserDto;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserEditView extends ModalViewWithUiHandlers<UserEditUiHandlers> implements UserEditPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, UserEditView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Modal userEditView;

	@UiField
	LoadingIndicator loadingIndicator;

	@UiField
	ErrorIndicator errorIndicator;

	@UiField
	ErrorAwareForm form;

	@UiField
	FieldSet fieldSet;

	@UiField
	Input nameField;

	@UiField
	Input emailField;

	@UiField
	Input passwordField;

	@UiField
	Input repeatPasswordField;

	@UiField
	ListBox roleField;

	@UiField
	Button deleteButton;

	private LoadingState loadingState;

	private UserDto user;

	private List<ErrorDto> errors;

	private final Map<RoleDto, Integer> roleToIndex = new HashMap<>();
	private final Map<Integer, RoleDto> indexToRole = new HashMap<>();

	@Inject
	public UserEditView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));

		roleField.addItem(Messages.INSTANCE.userEditRoleUser());
		roleField.addItem(Messages.INSTANCE.userEditRoleAdmin());

		roleToIndex.put(RoleDto.USER, 0);
		roleToIndex.put(RoleDto.ADMIN, 1);

		indexToRole.put(0, RoleDto.USER);
		indexToRole.put(1, RoleDto.ADMIN);
	}

	@Override
	public boolean isEnabled() {
		return fieldSet.isEnabled();
	}

	@Override
	public void setEnabled(boolean aEnabled) {
		fieldSet.setEnabled(aEnabled);
	}

	@Override
	public LoadingState getLoadingState() {
		return loadingState;
	}

	@Override
	public void setLoadingState(LoadingState aLoadingState) {

		loadingState = aLoadingState;

		updateLoadingState();
	}

	@Override
	public UserDto getUser() {
		return user;
	}

	@Override
	public void setUser(UserDto aUser) {

		user = aUser;

		updateUser();
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
	public void setFocus() {
		nameField.setFocus(true);
	}

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent aEvent) {
		if (getUser() != null) {
			requestModification();
		} else {
			requestCreation();
		}
	}

	@UiHandler("deleteButton")
	void onDeleteClick(ClickEvent aEvent) {
		getUiHandlers().onDeletionRequested();
	}

	@SuppressWarnings("GwtUiHandlerErrors")
	@UiHandler("userEditView")
	void onViewShown(ModalShownEvent aEvent) {
		setFocus();
	}

	private void updateLoadingState() {
		loadingIndicator.setVisible(getLoadingState() == LoadingState.LOADING);
		errorIndicator.setVisible(getLoadingState() == LoadingState.ERROR);
		form.setVisible(getLoadingState() == LoadingState.LOADED);
	}

	private void updateUser() {

		userEditView.setTitle(getUser() != null ? Messages.INSTANCE.userEditModificationTitle() : Messages.INSTANCE.userEditCreationTitle());

		nameField.setText(getUser() != null ? getUser().getName() : "");
		emailField.setText(getUser() != null ? getUser().getEmail() : "");

		passwordField.setText("");
		repeatPasswordField.setText("");

		if (getUser() != null) {
			roleField.setSelectedIndex(roleToIndex.get(getUser().getRole()));
		} else {
			roleField.setSelectedIndex(roleToIndex.get(RoleDto.USER));
		}

		deleteButton.setVisible(getUser() != null);
	}

	private void updateErrors() {
		form.setErrors(getErrors());
	}

	private void requestCreation() {

		CreateUserCommandDto command = new CreateUserCommandDto();

		command.setName(nameField.getText());
		command.setEmail(emailField.getText());
		command.setRole(indexToRole.get(roleField.getSelectedIndex()));

		command.setPassword(passwordField.getText());
		command.setRepeatPassword(repeatPasswordField.getText());

		getUiHandlers().onCreationRequested(command);
	}

	private void requestModification() {

		UpdateUserCommandDto command = new UpdateUserCommandDto();

		command.setId(getUser().getId());

		command.setName(nameField.getText());
		command.setEmail(emailField.getText());
		command.setRole(indexToRole.get(roleField.getSelectedIndex()));

		command.setPassword(passwordField.getText());
		command.setRepeatPassword(repeatPasswordField.getText());

		getUiHandlers().onModificationRequested(command);
	}

}
