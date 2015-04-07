package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class LogMessageService {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public LogMessageService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getLog(int aPageNumber, LogMessageDto.Type aType, Date aMinDate, Date aMaxDate,
								   final OperationCallback<PagedListDto<LogMessageDto>> aCallback) {

		log.info("Getting log messages page [" + aPageNumber + "]...");

		return new RequestAdapter(apiService.getLog(aPageNumber, aType, aMinDate, aMaxDate, new MethodCallbackAdapter<>(new OperationCallback<PagedListDto<LogMessageDto>>() {
			@Override
			public void onSuccess(PagedListDto<LogMessageDto> aPage) {

				log.info("[" + aPage.getContent().size() + "] of [" + aPage.getTotalElements() + "] log messages returned.");

				aCallback.onSuccess(aPage);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Could not get log.");

				aCallback.onError(aErrors);
			}
		})));
	}

}
