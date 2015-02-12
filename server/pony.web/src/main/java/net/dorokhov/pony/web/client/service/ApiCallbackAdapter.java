package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.common.OperationCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

public class ApiCallbackAdapter {

	public <T> MethodCallback<T> handle(OperationCallback<T> aCallback) {
		return new MethodCallback<T>() {
			@Override
			public void onFailure(Method method, Throwable exception) {

			}

			@Override
			public void onSuccess(Method method, T response) {

			}
		};
	}

}
