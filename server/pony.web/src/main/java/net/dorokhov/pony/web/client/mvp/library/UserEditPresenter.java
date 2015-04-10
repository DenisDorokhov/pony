package net.dorokhov.pony.web.client.mvp.library;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.UserService;
import net.dorokhov.pony.web.shared.UserDto;

public class UserEditPresenter extends PresenterWidget<UserEditPresenter.MyView> implements UserEditUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<UserEditUiHandlers> {

		public UserDto getUser();

		public void setUser(UserDto aUser);

	}

	private final UserService userService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public UserEditPresenter(EventBus eventBus, MyView view, UserService aUserService, ErrorNotifier aErrorNotifier) {

		super(eventBus, view);

		userService = aUserService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	public UserDto getUser() {
		return getView().getUser();
	}

	public void setUser(UserDto aUser) {
		getView().setUser(aUser);
	}

}
