package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.LogMessageService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.PagedListDto;

import javax.inject.Inject;
import java.util.List;

public class LogPresenter extends PresenterWidget<LogPresenter.MyView> implements LogUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<LogUiHandlers> {}

	private final LogMessageService logService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public LogPresenter(EventBus aEventBus, MyView aView, LogMessageService aLogService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		logService = aLogService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	public OperationRequest onLogMessagesRequested(int aPageNumber, final OperationCallback<PagedListDto<LogMessageDto>> aCallback) {
		return logService.getLog(aPageNumber, null, null, null, new OperationCallback<PagedListDto<LogMessageDto>>() {

			@Override
			public void onSuccess(PagedListDto<LogMessageDto> aPage) {
				aCallback.onSuccess(aPage);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				errorNotifier.notifyOfErrors(aErrors);

				aCallback.onError(aErrors);
			}
		});
	}
}
