package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.LibraryScanner;
import net.dorokhov.pony.web.client.service.ScanJobService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;

import javax.inject.Inject;
import java.util.List;

public class ScanningPresenter extends PresenterWidget<ScanningPresenter.MyView> implements LibraryScanner.Delegate, ScanningUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<ScanningUiHandlers> {

		public enum ScanState {
			INACTIVE, SCANNING
		}

		public void reloadScanJobs();

		public ScanState getScanState();

		public void setScanState(ScanState aScanState);

		public ScanStatusDto getProgress();

		public void setProgress(ScanStatusDto aStatus);

	}

	private final LibraryScanner libraryScanner;

	private final ScanJobService scanJobService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public ScanningPresenter(EventBus aEventBus, MyView aView,
							 LibraryScanner aLibraryScanner, ScanJobService aScanJobService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		libraryScanner = aLibraryScanner;
		scanJobService = aScanJobService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {

		super.onBind();

		libraryScanner.addDelegate(this);
	}

	@Override
	protected void onUnbind() {

		libraryScanner.removeDelegate(this);

		super.onUnbind();
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		libraryScanner.updateStatus();
	}

	@Override
	public OperationRequest onScanJobsRequested(int aPageNumber, final OperationCallback<PagedListDto<ScanJobDto>> aCallback) {
		return scanJobService.getScanJobs(aPageNumber, new OperationCallback<PagedListDto<ScanJobDto>>() {

			@Override
			public void onSuccess(PagedListDto<ScanJobDto> aPage) {
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
	public void onScanRequested() {
		libraryScanner.scan();
	}

	@Override
	public void onScanStarted(LibraryScanner aLibraryScanner) {
		getView().setScanState(MyView.ScanState.SCANNING);
	}

	@Override
	public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus) {
		getView().setProgress(aStatus);
	}

	@Override
	public void onScanFinished(LibraryScanner aLibraryScanner) {

		getView().setProgress(null);
		getView().setScanState(MyView.ScanState.INACTIVE);

		getView().reloadScanJobs();
	}

}
