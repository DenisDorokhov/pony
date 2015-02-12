package net.dorokhov.pony.web.client.common;

import com.google.gwt.http.client.Request;

public class RequestAdapter implements OperationRequest {

	private final Request request;

	public RequestAdapter(Request aRequest) {
		request = aRequest;
	}

	@Override
	public void cancel() {
		request.cancel();
	}

}
