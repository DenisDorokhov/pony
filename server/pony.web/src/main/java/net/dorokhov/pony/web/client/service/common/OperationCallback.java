package net.dorokhov.pony.web.client.service.common;

import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.List;

public interface OperationCallback<T> {

	public void onSuccess(T aData);

	public void onError(List<ErrorDto> aErrors);

}
