package net.dorokhov.pony.web.client.service.api;

import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.shared.ErrorCode;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.Arrays;

public class MethodCallbackAdapter<T> implements MethodCallback<ResponseDto<T>> {

	private final OperationCallback<T> operationCallback;

	public MethodCallbackAdapter(OperationCallback<T> aOperationCallback) {
		operationCallback = aOperationCallback;
	}

	@Override
	public void onSuccess(Method aMethod, ResponseDto<T> aResponse) {
		if (aResponse.isSuccessful()) {
			operationCallback.onSuccess(aResponse.getData());
		} else {
			operationCallback.onError(aResponse.getErrors());
		}
	}

	@Override
	public void onFailure(Method aMethod, Throwable aException) {

		ErrorDto error;

		if (aException.getMessage() != null && aException.getMessage().length() > 0) {
			error = new ErrorDto(ErrorCode.CLIENT_REQUEST_FAILED,
					"An error has occurred when making server request: \"" + aException.getMessage() + "\".",
					Arrays.asList(aException.getMessage()));
		} else {
			error = new ErrorDto(ErrorCode.CLIENT_OFFLINE, "Could not make server request. Are you online?");
		}

		operationCallback.onError(Arrays.asList(error));
	}

}
