package net.dorokhov.pony.web.client.service.common;

import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.List;

public class NoOpOperationCallback<T> implements OperationCallback<T> {

	@Override
	public void onSuccess(T aData) {
		onFinish(true, aData, null);
	}

	@Override
	public void onError(List<ErrorDto> aErrors) {
		onFinish(false, null, aErrors);
	}

	public void onFinish(boolean aSuccess, T aData, List<ErrorDto> aErrors) {}

}
