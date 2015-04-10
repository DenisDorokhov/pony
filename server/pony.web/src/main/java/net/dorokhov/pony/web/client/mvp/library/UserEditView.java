package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.UserDto;
import org.gwtbootstrap3.client.ui.Modal;

public class UserEditView extends ModalViewWithUiHandlers<UserEditUiHandlers> implements UserEditPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, UserEditView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Modal userEditView;

	private UserDto user;

	@Inject
	public UserEditView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));
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

	private void updateUser() {
		userEditView.setTitle(getUser() != null ? Messages.INSTANCE.userEditModificationTitle() : Messages.INSTANCE.userEditCreationTitle());
	}

}
