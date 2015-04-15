package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.ErrorAwareForm;
import net.dorokhov.pony.web.client.control.status.ErrorIndicator;
import net.dorokhov.pony.web.client.control.status.LoadingIndicator;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.ConfigDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsView extends ModalViewWithUiHandlers<SettingsUiHandlers> implements SettingsPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, SettingsView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	LoadingIndicator loadingIndicator;

	@UiField
	ErrorIndicator errorIndicator;

	@UiField
	ErrorAwareForm form;

	@UiField
	FieldSet fieldSet;

	@UiField
	ListBox autoScanField;

	private final Map<Integer, Integer> autoScanIntervalToIndex = new HashMap<>();
	private final Map<Integer, Integer> indexToAutoScanInterval = new HashMap<>();

	private LoadingState loadingState;

	private ConfigDto config;

	private List<ErrorDto> errors;

	@Inject
	public SettingsView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));

		autoScanField.addItem(Messages.INSTANCE.settingsAutoScanEveryHour());
		autoScanField.addItem(Messages.INSTANCE.settingsAutoScanEveryDay());
		autoScanField.addItem(Messages.INSTANCE.settingsAutoScanEveryWeek());
		autoScanField.addItem(Messages.INSTANCE.settingsAutoScanOff());

		autoScanIntervalToIndex.put(3600, 0);
		autoScanIntervalToIndex.put(86400, 1);
		autoScanIntervalToIndex.put(604800, 2);
		autoScanIntervalToIndex.put(0, 3);

		indexToAutoScanInterval.put(0, 3600);
		indexToAutoScanInterval.put(1, 86400);
		indexToAutoScanInterval.put(2, 604800);
		indexToAutoScanInterval.put(3, 0);
	}

	@Override
	public boolean isEnabled() {
		return fieldSet.isEnabled();
	}

	@Override
	public void setEnabled(boolean aEnabled) {
		fieldSet.setEnabled(aEnabled);
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
	public ConfigDto getConfig() {
		return config;
	}

	@Override
	public void setConfig(ConfigDto aConfig) {

		config = aConfig;

		updateConfig();
	}

	@Override
	public List<ErrorDto> getErrors() {

		if (errors == null) {
			errors = new ArrayList<>();
		}

		return errors;
	}

	@Override
	public void setErrors(List<ErrorDto> aErrors) {

		errors = aErrors;

		updateErrors();
	}

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent aClickEvent) {
		requestSave();
	}

	private void updateLoadingState() {
		loadingIndicator.setVisible(getLoadingState() == LoadingState.LOADING);
		errorIndicator.setVisible(getLoadingState() == LoadingState.ERROR);
		form.setVisible(getLoadingState() == LoadingState.LOADED);
	}

	private void updateConfig() {

		Integer indexToSelect = getConfig() != null ? autoScanIntervalToIndex.get(getConfig().getAutoScanInterval()) : null;
		if (indexToSelect == null) {
			indexToSelect = autoScanIntervalToIndex.get(0);
		}

		autoScanField.setSelectedIndex(indexToSelect);
	}

	private void updateErrors() {
		form.setErrors(getErrors());
	}

	private void requestSave() {

		ConfigDto config = new ConfigDto();

		Integer intervalValue = indexToAutoScanInterval.get(autoScanField.getSelectedIndex());
		if (intervalValue == 0) {
			intervalValue = null;
		}

		config.setAutoScanInterval(intervalValue);

		// TODO: set library folders
		config.setLibraryFolders(getConfig().getLibraryFolders());

		getUiHandlers().onSaveRequested(config);
	}

}
