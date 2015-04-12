package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class UserService {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public UserService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getUsers(int aPageNumber, final OperationCallback<PagedListDto<UserDto>> aCallback) {

		log.info("Getting users page [" + aPageNumber + "]...");

		return new RequestAdapter(apiService.getUsers(aPageNumber, new MethodCallbackAdapter<>(new OperationCallback<PagedListDto<UserDto>>() {
			@Override
			public void onSuccess(PagedListDto<UserDto> aPage) {

				log.info("[" + aPage.getContent().size() + "] of [" + aPage.getTotalElements() + "] users returned.");

				aCallback.onSuccess(aPage);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Could not get users.");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest getUser(final Long aId, final OperationCallback<UserDto> aCallback) {

		log.info("Getting user [" + aId + "]...");

		return new RequestAdapter(apiService.getUser(aId, new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				log.info("User [" + aUser.getId() + "] returned.");

				aCallback.onSuccess(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not get user [" + aId + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest createUser(CreateUserCommandDto aCommand, final OperationCallback<UserDto> aCallback) {

		log.info("Creating user...");

		return new RequestAdapter(apiService.createUser(aCommand, new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				log.info("User [" + aUser.getId() + "] created.");

				aCallback.onSuccess(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not create user.");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest updateUser(final UpdateUserCommandDto aCommand, final OperationCallback<UserDto> aCallback) {

		log.info("Updating user [" + aCommand.getId() + "]...");

		return new RequestAdapter(apiService.updateUser(aCommand, new MethodCallbackAdapter<>(new OperationCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto aUser) {

				log.info("User [" + aUser.getId() + "] updated.");

				aCallback.onSuccess(aUser);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not update user [" + aCommand.getId() + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest deleteUser(final Long aId, final OperationCallback<Void> aCallback) {

		log.info("Deleting user [" + aId + "]...");

		return new RequestAdapter(apiService.deleteUser(aId, new MethodCallbackAdapter<>(new OperationCallback<Object>() {
			@Override
			public void onSuccess(Object aData) {

				log.info("User [" + aId + "] deleted.");

				aCallback.onSuccess(null);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not delete user [" + aId + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

}
