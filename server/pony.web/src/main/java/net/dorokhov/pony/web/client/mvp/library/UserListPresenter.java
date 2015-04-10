package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.UserService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;
import java.util.List;

public class UserListPresenter extends PresenterWidget<UserListPresenter.MyView> implements UserListUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<UserListUiHandlers> {}

	private final UserService userService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public UserListPresenter(EventBus eventBus, MyView view, UserService aUserService, ErrorNotifier aErrorNotifier) {

		super(eventBus, view);

		userService = aUserService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
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
		// TODO: implement
	}

	@Override
	public void onUserModificationRequester(UserDto aUser) {
		// TODO: implement
	}

}
