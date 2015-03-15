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

	}

	private final LibraryScanner libraryScanner;

	@Inject
	public ScanningPresenter(EventBus aEventBus, MyView aView, LibraryScanner aLibraryScanner) {

		super(aEventBus, aView);

		libraryScanner = aLibraryScanner;
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
	public void onScanRequested() {

	}

	@Override
	public void onScanStarted(LibraryScanner aLibraryScanner) {

	}

	@Override
	public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus) {

	}

	@Override
	public void onScanFinished(LibraryScanner aLibraryScanner) {

	}

}
