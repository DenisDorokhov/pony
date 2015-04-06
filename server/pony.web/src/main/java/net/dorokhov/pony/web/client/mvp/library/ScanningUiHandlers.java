package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;

public interface ScanningUiHandlers extends UiHandlers {

	public OperationRequest onScanJobsRequested(int aPageNumber, OperationCallback<PagedListDto<ScanJobDto>> aCallback);

	public void onScanRequested();

}
