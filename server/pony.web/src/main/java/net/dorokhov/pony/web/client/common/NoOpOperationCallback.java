package net.dorokhov.pony.web.client.common;

import java.util.List;

public class NoOpOperationCallback<T> implements OperationCallback<T> {

	@Override
	public void onSuccess(T aData) {}

	@Override
	public void onError(List aErrors) {}

}
