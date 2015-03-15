package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.service.LibraryScanner;
import net.dorokhov.pony.web.shared.ScanStatusDto;

import javax.inject.Inject;

public class ScanningPresenter extends PresenterWidget<ScanningPresenter.MyView> implements LibraryScanner.Delegate, ScanningUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<ScanningUiHandlers> {

		public enum State {
			INACTIVE, SCANNING
		}

		public State getState();

		public void setState(State aState);

		public ScanStatusDto getProgress();

		public void setProgress(ScanStatusDto aStatus);

	}

	private final LibraryScanner libraryScanner;

	@Inject
	public ScanningPresenter(EventBus aEventBus, MyView aView, LibraryScanner aLibraryScanner) {

		super(aEventBus, aView);

		libraryScanner = aLibraryScanner;

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
	public void onScanRequested() {
		libraryScanner.scan();
	}

	@Override
	public void onScanStarted(LibraryScanner aLibraryScanner) {
		getView().setState(MyView.State.SCANNING);
	}

	@Override
	public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus) {
		getView().setProgress(aStatus);
	}

	@Override
	public void onScanFinished(LibraryScanner aLibraryScanner) {
		getView().setProgress(null);
		getView().setState(MyView.State.INACTIVE);
	}

}
