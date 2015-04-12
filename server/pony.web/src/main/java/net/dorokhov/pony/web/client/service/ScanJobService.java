package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class ScanJobService {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public ScanJobService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getScanJobs(int aPageNumber, final OperationCallback<PagedListDto<ScanJobDto>> aCallback) {

		log.info("Getting scan jobs page [" + aPageNumber + "]...");

		return new RequestAdapter(apiService.getScanJobs(aPageNumber, new MethodCallbackAdapter<>(new OperationCallback<PagedListDto<ScanJobDto>>() {
			@Override
			public void onSuccess(PagedListDto<ScanJobDto> aPage) {

				log.info("[" + aPage.getContent().size() + "] of [" + aPage.getTotalElements() + "] scan jobs returned.");

				aCallback.onSuccess(aPage);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Could not get scan jobs.");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest getScanJob(final Long aId, final OperationCallback<ScanJobDto> aCallback) {

		log.info("Getting scan job [" + aId + "]...");

		return new RequestAdapter(apiService.getScanJob(aId, new MethodCallbackAdapter<>(new OperationCallback<ScanJobDto>() {
			@Override
			public void onSuccess(ScanJobDto aJob) {

				log.info("Scan job [" + aJob.getId() + "] returned.");

				aCallback.onSuccess(aJob);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not get scan job [" + aId + "].");

				aCallback.onError(aErrors);
			}
		})));
	}

}
