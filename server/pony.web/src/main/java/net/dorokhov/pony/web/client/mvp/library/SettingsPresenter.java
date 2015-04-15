package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import net.dorokhov.pony.web.client.mvp.common.HasLoadingState;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.service.ConfigService;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ConfigDto;
import net.dorokhov.pony.web.shared.ErrorDto;

import javax.inject.Inject;
import java.util.List;

public class SettingsPresenter extends PresenterWidget<SettingsPresenter.MyView> implements SettingsUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<SettingsUiHandlers>, HasLoadingState, HasEnabled {

		public ConfigDto getConfig();

		public void setConfig(ConfigDto aConfig);

		public List<ErrorDto> getErrors();

		public void setErrors(List<ErrorDto> aErrors);

	}

	private final ConfigService configService;

	private final ErrorNotifier errorNotifier;

	private OperationRequest currentRequest;

	@Inject
	public SettingsPresenter(EventBus aEventBus, MyView aView,
							 ConfigService aConfigService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		configService = aConfigService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		loadConfig();
	}

	@Override
	public void onSaveRequested(ConfigDto aConfig) {

		getView().setEnabled(false);

		configService.saveConfig(aConfig, new OperationCallback<ConfigDto>() {
			@Override
			public void onSuccess(ConfigDto aConfig) {

				getView().setErrors(null);

				getView().setEnabled(true);

				// TODO: fire config save event and offer scanning?

				getView().hide();
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				getView().setErrors(aErrors);

				getView().setEnabled(true);
			}
		});
	}

	private void loadConfig() {

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		getView().setErrors(null);

		getView().setLoadingState(LoadingState.LOADING);

		currentRequest = configService.getConfig(new OperationCallback<ConfigDto>() {
			@Override
			public void onSuccess(ConfigDto aConfig) {

				getView().setConfig(aConfig);

				getView().setLoadingState(LoadingState.LOADED);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				errorNotifier.notifyOfErrors(aErrors);

				getView().setLoadingState(LoadingState.ERROR);
			}
		});
	}

}
