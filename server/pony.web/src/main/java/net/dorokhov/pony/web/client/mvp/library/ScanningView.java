package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.PagedListView;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ProgressType;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class ScanningView extends ModalViewWithUiHandlers<ScanningUiHandlers> implements ScanningPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, ScanningView> {}

	@SuppressWarnings("GwtCssResourceErrors")
	interface MyStyle extends CssResource {

		String jobStatus();

		String jobStatusStarting();
		String jobStatusStarted();
		String jobStatusComplete();
		String jobStatusFailed();
		String jobStatusInterrupted();

	}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final NumberFormat PROGRESS_FORMAT = NumberFormat.getPercentFormat();
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(Messages.INSTANCE.dateFormatTechnical());

	@UiField
	MyStyle style;

	@UiField
	Label statusLabel;

	@UiField
	Progress progressContainer;

	@UiField
	ProgressBar progressBar;

	@UiField
	Button scanButton;

	@UiField(provided = true)
	PagedListView jobPagedView;

	private ScanState scanState;

	private ScanStatusDto progress;

	@Inject
	public ScanningView(EventBus aEventBus) {

		super(aEventBus);

		initGrid();

		initWidget(uiBinder.createAndBindUi(this));

		setScanState(ScanState.INACTIVE);
	}

	@Override
	public void reloadScanJobs() {
		jobPagedView.reload();
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

	@UiHandler("scanningView")
	void onPagedListHidden(ModalHiddenEvent aEvent) {
		jobPagedView.clear();
	}

	@UiHandler("scanningView")
	void onPagedListShown(ModalShownEvent aEvent) {
		jobPagedView.reload();
	}

	private void initGrid() {

		final List<String> headers = Arrays.asList(
				Messages.INSTANCE.scanningColumnStarted(),
				Messages.INSTANCE.scanningColumnUpdated(),
				Messages.INSTANCE.scanningColumnStatus(),
				Messages.INSTANCE.scanningColumnLastMessage()
		);
		final List<String> widths = Arrays.asList(
				"150px", "150px", "100px", null
		);
		final List<TextColumn<ScanJobDto>> columns = Arrays.asList(
				new TextColumn<ScanJobDto>() {
					@Override
					public String getValue(ScanJobDto aJob) {
						return DATE_FORMAT.format(aJob.getCreationDate());
					}
				},
				new TextColumn<ScanJobDto>() {
					@Override
					public String getValue(ScanJobDto aJob) {
						return aJob.getUpdateDate() != null ? DATE_FORMAT.format(aJob.getUpdateDate()) : "";
					}
				},
				new TextColumn<ScanJobDto>() {
					@Override
					public String getValue(ScanJobDto aJob) {

						switch (aJob.getStatus()) {
							case STARTING:
								return Messages.INSTANCE.scanningJobStatusStarting();
							case STARTED:
								return Messages.INSTANCE.scanningJobStatusStarted();
							case COMPLETE:
								return Messages.INSTANCE.scanningJobStatusComplete();
							case FAILED:
								return Messages.INSTANCE.scanningJobStatusFailed();
							case INTERRUPTED:
								return Messages.INSTANCE.scanningJobStatusInterrupted();
						}

						return String.valueOf(aJob.getStatus());
					}

					@Override
					public String getCellStyleNames(Cell.Context aContext, ScanJobDto aJob) {

						String result = style.jobStatus() + " ";

						switch (aJob.getStatus()) {
							case STARTING:
								result += style.jobStatusStarting();
								break;
							case STARTED:
								result += style.jobStatusStarted();
								break;
							case COMPLETE:
								result += style.jobStatusComplete();
								break;
							case FAILED:
								result += style.jobStatusFailed();
								break;
							case INTERRUPTED:
								result += style.jobStatusInterrupted();
								break;
						}

						return result;
					}
				},
				new TextColumn<ScanJobDto>() {
					@Override
					public String getValue(ScanJobDto aJob) {
						return FormatUtils.formatLog(aJob.getLogMessage());
					}
				}
		);

		jobPagedView = new PagedListView<>(new PagedListView.DataSource<ScanJobDto>() {

			@Override
			public int getColumnCount() {
				return columns.size();
			}

			@Override
			public Column<ScanJobDto, String> getColumn(int aIndex) {
				return columns.get(aIndex);
			}

			@Override
			public String getColumnWidth(int aIndex) {
				return widths.get(aIndex);
			}

			@Override
			public String getHeader(int aIndex) {
				return headers.get(aIndex);
			}

			@Override
			public String getPagerLabel(PagedListDto<ScanJobDto> aPagedList) {
				return Messages.INSTANCE.scanningPager(aPagedList.getPageNumber() + 1, aPagedList.getTotalPages(), aPagedList.getContent().size(), aPagedList.getTotalElements());
			}

			@Override
			public OperationRequest requestPagedList(int aPageNumber, OperationCallback<PagedListDto<ScanJobDto>> aCallback) {
				return getUiHandlers().onScanJobsRequested(aPageNumber, aCallback);
			}
		});
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

}
