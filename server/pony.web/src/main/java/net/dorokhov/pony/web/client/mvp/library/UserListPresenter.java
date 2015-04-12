package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.event.UserCreationEvent;
import net.dorokhov.pony.web.client.event.UserDeletionEvent;
import net.dorokhov.pony.web.client.event.UserUpdateEvent;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.UserService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;
import java.util.List;

public class UserListPresenter extends PresenterWidget<UserListPresenter.MyView> implements UserListUiHandlers,
		UserCreationEvent.Handler, UserUpdateEvent.Handler, UserDeletionEvent.Handler {

	public interface MyView extends PopupView, HasUiHandlers<UserListUiHandlers> {

		public void reloadUsers();

	}

	private final UserEditPresenter userEditPresenter;

	private final UserService userService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public UserListPresenter(EventBus aEventBus, MyView aView,
							 UserEditPresenter aUserEditPresenter, UserService aUserService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		userEditPresenter = aUserEditPresenter;
		userService = aUserService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(UserCreationEvent.TYPE, this);
		addRegisteredHandler(UserUpdateEvent.TYPE, this);
		addRegisteredHandler(UserDeletionEvent.TYPE, this);
	}

	@Override
	public OperationRequest onUsersRequested(int aPageNumber, final OperationCallback<PagedListDto<UserDto>> aCallback) {
		return userService.getUsers(aPageNumber, new OperationCallback<PagedListDto<UserDto>>() {

			@Override
			public void onSuccess(PagedListDto<UserDto> aPage) {
				aCallback.onSuccess(aPage);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				errorNotifier.notifyOfErrors(aErrors);

				aCallback.onError(aErrors);
			}
		});
	}

	@Override
	public void onUserCreationRequested() {

		userEditPresenter.setUser(null);

		addToPopupSlot(userEditPresenter);
	}

	@Override
	public void onUserModificationRequester(UserDto aUser) {

		userEditPresenter.setUser(aUser);

		addToPopupSlot(userEditPresenter);
	}

	@Override
	public void onUserCreation(UserCreationEvent aEvent) {
		getView().reloadUsers();
	}

	@Override
	public void onUserUpdate(UserUpdateEvent aEvent) {
		getView().reloadUsers();
	}

	@Override
	public void onUserDeletion(UserDeletionEvent aEvent) {
		getView().reloadUsers();
	}

}
