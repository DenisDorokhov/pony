package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import net.dorokhov.pony.web.client.control.ErrorAwareForm;
import net.dorokhov.pony.web.client.control.FieldAwareFormGroup;
import net.dorokhov.pony.web.client.control.status.ErrorIndicator;
import net.dorokhov.pony.web.client.control.status.LoadingIndicator;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.ConfigDto;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.LibraryFolderDto;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.IconType;

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

	@UiField
	FlowPanel libraryFolderContainer;

	private final Map<Integer, Integer> autoScanIntervalToIndex = new HashMap<>();
	private final Map<Integer, Integer> indexToAutoScanInterval = new HashMap<>();

	private final List<LibraryFolderField> libraryFolderFields = new ArrayList<>();
	private final List<HandlerRegistration> libraryFolderHandlers = new ArrayList<>();

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

		libraryFolderContainer.clear();
		libraryFolderFields.clear();

		if (getConfig() != null) {

			int i = 0;

			do {

				LibraryFolderField field = buildLibraryFolderFormGroup();

				if (i < getConfig().getLibraryFolders().size()) {

					LibraryFolderDto folder = getConfig().getLibraryFolders().get(i);

					field.getTextBox().setText(folder.getPath());
				}

				libraryFolderContainer.add(field.getFormGroup());
				libraryFolderFields.add(field);

				i++;

			} while (i < getConfig().getLibraryFolders().size());
		}

		refreshLibraryFolderFields();
	}

	private void refreshLibraryFolderFields() {

		while (libraryFolderHandlers.size() > 0) {
			libraryFolderHandlers.get(0).removeHandler();
			libraryFolderHandlers.remove(0);
		}

		for (int i = 0; i < libraryFolderFields.size(); i++) {

			final LibraryFolderField field = libraryFolderFields.get(i);

			field.getAddButton().setEnabled(libraryFolderFields.size() < 5);
			field.getRemoveButton().setEnabled(libraryFolderFields.size() > 1);

			field.getFormGroup().setFieldName("libraryFolders[" + i + "].path");

			libraryFolderHandlers.add(field.getAddButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					addLibraryFolder();
				}
			}));
			libraryFolderHandlers.add(field.getRemoveButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					removeLibraryFolder(field);
				}
			}));
		}
	}

	private void addLibraryFolder() {

		LibraryFolderField field = buildLibraryFolderFormGroup();

		libraryFolderContainer.add(field.getFormGroup());
		libraryFolderFields.add(field);

		field.getTextBox().setFocus(true);

		refreshLibraryFolderFields();
	}

	private void removeLibraryFolder(LibraryFolderField aField) {

		int index = libraryFolderFields.indexOf(aField);

		libraryFolderContainer.remove(index);
		libraryFolderFields.remove(index);

		refreshLibraryFolderFields();
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

		for (LibraryFolderField field : libraryFolderFields) {
			config.getLibraryFolders().add(new LibraryFolderDto(field.getTextBox().getText()));
		}

		getUiHandlers().onSaveRequested(config);
	}

	private LibraryFolderField buildLibraryFolderFormGroup() {

		FieldAwareFormGroup formGroup = new FieldAwareFormGroup();

		TextBox textBox = new TextBox();

		textBox.setPlaceholder(Messages.INSTANCE.settingsLibraryFolderPlaceholder());

		InputGroupButton groupButton = new InputGroupButton();

		Button addButton = new Button();

		addButton.setIcon(IconType.PLUS);

		Button removeButton = new Button();

		removeButton.setIcon(IconType.MINUS);

		groupButton.add(addButton);
		groupButton.add(removeButton);

		InputGroup inputGroup = new InputGroup();

		inputGroup.add(textBox);

		inputGroup.add(groupButton);

		formGroup.add(inputGroup);

		return new LibraryFolderField(formGroup, textBox, addButton, removeButton);
	}

	private class LibraryFolderField {

		private final FieldAwareFormGroup formGroup;

		private final TextBox textBox;

		private final Button addButton;

		private final Button removeButton;

		public LibraryFolderField(FieldAwareFormGroup aFormGroup, TextBox aTextBox, Button aAddButton, Button aRemoveButton) {
			formGroup = aFormGroup;
			textBox = aTextBox;
			addButton = aAddButton;
			removeButton = aRemoveButton;
		}

		public FieldAwareFormGroup getFormGroup() {
			return formGroup;
		}

		public TextBox getTextBox() {
			return textBox;
		}

		public Button getAddButton() {
			return addButton;
		}

		public Button getRemoveButton() {
			return removeButton;
		}

	}

}
