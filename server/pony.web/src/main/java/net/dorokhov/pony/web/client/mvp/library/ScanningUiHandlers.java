package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ScanningUiHandlers extends UiHandlers {

	public void onScanJobsPageRequested(int aPageNumber);

	public void onScanRequested();

}
