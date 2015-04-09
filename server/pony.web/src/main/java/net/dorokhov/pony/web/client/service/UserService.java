package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;

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

}
