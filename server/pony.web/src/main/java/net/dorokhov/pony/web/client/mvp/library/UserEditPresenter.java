package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.event.UserCreationEvent;
import net.dorokhov.pony.web.client.event.UserDeletionEvent;
import net.dorokhov.pony.web.client.event.UserUpdateEvent;
import net.dorokhov.pony.web.client.mvp.common.HasLoadingState;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.AuthenticationManager;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.UserService;
import net.dorokhov.pony.web.client.service.common.NoOpOperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.UserDto;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;

import java.util.List;

public class UserEditPresenter extends PresenterWidget<UserEditPresenter.MyView> implements UserEditUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<UserEditUiHandlers>, HasLoadingState, HasEnabled {

		public UserDto getUser();

		public void setUser(UserDto aUser);

		public List<ErrorDto> getErrors();

		public void setErrors(List<ErrorDto> aErrors);

	}

	private final UserService userService;

	private final ErrorNotifier errorNotifier;

	private final AuthenticationManager authenticationManager;

	private OperationRequest currentRequest;

	@Inject
	public UserEditPresenter(EventBus eventBus, MyView view,
							 UserService aUserService, ErrorNotifier aErrorNotifier, AuthenticationManager aAuthenticationManager) {

		super(eventBus, view);

		userService = aUserService;
		errorNotifier = aErrorNotifier;
		authenticationManager = aAuthenticationManager;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		loadUser();
	}

	@Override
	public void onCreationRequested(CreateUserCommandDto aCommand) {

		getView().setEnabled(false);

		userService.createUser(aCommand, new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				getView().setErrors(null);
				getView().setUser(aUser);

				getView().setEnabled(true);

				authenticationManager.updateStatus(new NoOpOperationCallback<UserDto>());

				getEventBus().fireEvent(new UserCreationEvent(aUser));

				getView().hide();
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				getView().setErrors(aErrors);

				getView().setEnabled(true);
			}
		});
	}

	@Override
	public void onModificationRequested(UpdateUserCommandDto aCommand) {

		getView().setEnabled(false);

		userService.updateUser(aCommand, new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				getView().setErrors(null);
				getView().setUser(aUser);

				getView().setEnabled(true);

				authenticationManager.updateStatus(new NoOpOperationCallback<UserDto>());

				getEventBus().fireEvent(new UserUpdateEvent(aUser));

				getView().hide();
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				getView().setErrors(aErrors);

				getView().setEnabled(true);
			}
		});
	}

	@Override
	public void onDeletionRequested() {
		if (getUser() != null && Window.confirm(Messages.INSTANCE.userEditDeletionConfirmation())) {

			getView().setEnabled(false);

			userService.deleteUser(getUser().getId(), new OperationCallback<Void>() {
				@Override
				public void onSuccess(Void aData) {

					getView().setErrors(null);

					getView().setEnabled(true);

					getEventBus().fireEvent(new UserDeletionEvent(getUser().getId()));

					getView().hide();
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					getView().setErrors(aErrors);

					getView().setEnabled(true);
				}
			});
		}
	}

	public UserDto getUser() {
		return getView().getUser();
	}

	public void setUser(UserDto aUser) {

		getView().setUser(aUser);

		if (isVisible()) {
			loadUser();
		}
	}

	private void loadUser() {

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		getView().setErrors(null);

		if (getUser() != null) {

			getView().setLoadingState(LoadingState.LOADING);

			currentRequest = userService.getUser(getUser().getId(), new OperationCallback<UserDto>() {
				@Override
				public void onSuccess(UserDto aUser) {

					getView().setUser(aUser);

					getView().setLoadingState(LoadingState.LOADED);
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					errorNotifier.notifyOfErrors(aErrors);

					getView().setLoadingState(LoadingState.ERROR);
				}
			});

		} else {
			getView().setLoadingState(LoadingState.LOADED);
		}
	}

}
