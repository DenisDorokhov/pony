package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ConfigDto;
import net.dorokhov.pony.web.shared.ErrorDto;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class ConfigService {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public ConfigService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getConfig(final OperationCallback<ConfigDto> aCallback) {

		log.info("Getting config...");

		return new RequestAdapter(apiService.getConfig(new MethodCallbackAdapter<>(new OperationCallback<ConfigDto>() {
			@Override
			public void onSuccess(ConfigDto aConfig) {

				log.info("Config returned.");

				aCallback.onSuccess(aConfig);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Could not get config.");

				aCallback.onError(aErrors);
			}
		})));
	}

	public OperationRequest saveConfig(ConfigDto aConfig, final OperationCallback<ConfigDto> aCallback) {

		log.info("Saving config...");

		return new RequestAdapter(apiService.saveConfig(aConfig, new MethodCallbackAdapter<>(new OperationCallback<ConfigDto>() {
			@Override
			public void onSuccess(ConfigDto aConfig) {

				log.info("Config saved.");

				aCallback.onSuccess(aConfig);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.severe("Could not save config.");

				aCallback.onError(aErrors);
			}
		})));
	}

}
