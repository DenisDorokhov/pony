package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.PagedListDto;

public interface LogUiHandlers extends UiHandlers {

	public OperationRequest onLogMessagesRequested(int aPageNumber, OperationCallback<PagedListDto<LogMessageDto>> aCallback);

}
