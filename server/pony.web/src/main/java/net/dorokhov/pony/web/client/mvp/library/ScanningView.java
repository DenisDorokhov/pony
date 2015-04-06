package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.LogMessages;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ProgressType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import javax.inject.Inject;
import java.util.ArrayList;

public class ScanningView extends ModalViewWithUiHandlers<ScanningUiHandlers> implements ScanningPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, ScanningView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final NumberFormat PROGRESS_FORMAT = NumberFormat.getPercentFormat();

	@UiField
	Label statusLabel;

	@UiField
	Progress progressContainer;

	@UiField
	ProgressBar progressBar;

	@UiField
	Button scanButton;

	@UiField
	Pager jobsPager;

	@UiField
	DataGrid<ScanJobDto> jobsTable;

	private LoadingState loadingState;

	private PagedListDto<ScanJobDto> scanJobs;

	private ScanState scanState;

	private ScanStatusDto progress;

	@Inject
	public ScanningView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));

		setScanState(ScanState.INACTIVE);

		jobsPager.addPreviousClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiHandlers().onScanJobsPageRequested(scanJobs.getPageNumber() - 1);
			}
		});
		jobsPager.addNextClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUiHandlers().onScanJobsPageRequested(scanJobs.getPageNumber() + 1);
			}
		});

		jobsTable.addColumn(new TextColumn<ScanJobDto>() {
			@Override
			public String getValue(ScanJobDto aJob) {
				return String.valueOf(aJob.getCreationDate());
			}
		}, "Started");
		jobsTable.addColumn(new TextColumn<ScanJobDto>() {
			@Override
			public String getValue(ScanJobDto aJob) {
				return String.valueOf(aJob.getUpdateDate());
			}
		}, "Updated");
		jobsTable.addColumn(new TextColumn<ScanJobDto>() {
			@Override
			public String getValue(ScanJobDto aJob) {
				return String.valueOf(aJob.getStatus());
			}
		}, "Status");
		jobsTable.addColumn(new TextColumn<ScanJobDto>() {
			@Override
			public String getValue(ScanJobDto aJob) {
				return FormatUtils.formatMessage(LogMessages.INSTANCE,
						aJob.getLogMessage().getCode(), aJob.getLogMessage().getArguments(), aJob.getLogMessage().getText());
			}
		}, "Last Message");
	}

	@Override
	public LoadingState getLoadingState() {
		return loadingState;
	}

	@Override
	public void setLoadingState(LoadingState aLoadingState) {

		loadingState = aLoadingState;

		updateLoadingState();
	}

	@Override
	public PagedListDto<ScanJobDto> getScanJobs() {
		return scanJobs;
	}

	@Override
	public void setScanJobs(PagedListDto<ScanJobDto> aScanJobs) {

		scanJobs = aScanJobs;

		updateScanJobs();
	}

	@Override
	public ScanState getScanState() {
		return scanState;
	}

	@Override
	public void setScanState(ScanState aScanState) {

		scanState = aScanState;

		updateScanState();
	}

	@Override
	public ScanStatusDto getProgress() {
		return progress;
	}

	@Override
	public void setProgress(ScanStatusDto aStatus) {

		progress = aStatus;

		updateScanState();
	}

	@UiHandler("scanButton")
	void onScanButtonClick(ClickEvent aEvent) {
		getUiHandlers().onScanRequested();
	}

	private void updateLoadingState() {


	}

	private void updateScanJobs() {
		if (scanJobs != null) {

			jobsTable.setRowData(scanJobs.getContent());

			getPagerPrevious(jobsPager).setEnabled(scanJobs.getPageNumber() > 0);
			getPagerNext(jobsPager).setEnabled(scanJobs.getPageNumber() < scanJobs.getTotalPages() - 1);

		} else {

			jobsTable.setRowData(new ArrayList<ScanJobDto>());

			getPagerPrevious(jobsPager).setEnabled(false);
			getPagerNext(jobsPager).setEnabled(false);
		}
	}

	private void updateScanState() {

		scanButton.setEnabled(getScanState() == ScanState.INACTIVE);

		progressContainer.setActive(false);
		progressContainer.setType(ProgressType.DEFAULT);

		progressBar.setPercent(0);

		if (getScanState() == ScanState.INACTIVE) {

			statusLabel.setText(Messages.INSTANCE.scanningStatusInactive());

		} else {

			progressBar.setPercent(100);

			if (getProgress() != null) {

				if (getProgress().getProgress() < 0) {
					progressContainer.setActive(true);
					progressContainer.setType(ProgressType.STRIPED);
				} else {
					progressBar.setPercent(getProgress().getProgress() * 100);
				}

				statusLabel.setText(statusToMessage(getProgress()));

			} else {

				progressContainer.setActive(true);
				progressContainer.setType(ProgressType.STRIPED);

				statusLabel.setText(Messages.INSTANCE.scanningStatusStarting());
			}
		}
	}

	private String statusToMessage(ScanStatusDto aStatus) {

		switch (aStatus.getStepCode()) {
			case "preparing":
				return Messages.INSTANCE.scanningStatusStarting();
			case "searchingMediaFiles":
				return Messages.INSTANCE.scanningStatusSearchingMediaFiles();
			case "cleaningSongs":
				return Messages.INSTANCE.scanningStatusCleaningSongs(PROGRESS_FORMAT.format(aStatus.getProgress()));
			case "cleaningArtworks":
				return Messages.INSTANCE.scanningStatusCleaningArtworks(PROGRESS_FORMAT.format(aStatus.getProgress()));
			case "importingSongs":
				return Messages.INSTANCE.scanningStatusImportingSongs(PROGRESS_FORMAT.format(aStatus.getProgress()));
			case "normalizing":
				return Messages.INSTANCE.scanningStatusNormalizing(PROGRESS_FORMAT.format(aStatus.getProgress()));
		}

		return null;
	}

	private AnchorListItem getPagerPrevious(Pager aPager) {
		return (AnchorListItem)aPager.getWidget(aPager.getWidgetCount() - 2);
	}

	private AnchorListItem getPagerNext(Pager aPager) {
		return (AnchorListItem)aPager.getWidget(aPager.getWidgetCount() - 1);
	}

}
