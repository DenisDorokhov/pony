package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ProgressType;

import javax.inject.Inject;

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

	private State state;

	private ScanStatusDto progress;

	@Inject
	public ScanningView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));

		setState(State.INACTIVE);
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State aState) {

		state = aState;

		updateState();
	}

	@Override
	public ScanStatusDto getProgress() {
		return progress;
	}

	@Override
	public void setProgress(ScanStatusDto aStatus) {

		progress = aStatus;

		updateState();
	}

	@UiHandler("scanButton")
	void onScanButtonClick(ClickEvent aEvent) {
		getUiHandlers().onScanRequested();
	}

	private void updateState() {

		scanButton.setEnabled(getState() == State.INACTIVE);

		progressContainer.setActive(false);
		progressContainer.setType(ProgressType.DEFAULT);

		progressBar.setPercent(0);

		if (getState() == State.INACTIVE) {

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
