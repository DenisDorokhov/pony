package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;

public interface UserListUiHandlers extends UiHandlers {

	public OperationRequest onUsersRequested(int aPageNumber, OperationCallback<PagedListDto<UserDto>> aCallback);

	public void onUserCreationRequested();
	public void onUserModificationRequester(UserDto aUser);

}
