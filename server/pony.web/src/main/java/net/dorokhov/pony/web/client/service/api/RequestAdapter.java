package net.dorokhov.pony.web.client.service.api;

import com.google.gwt.http.client.Request;
import net.dorokhov.pony.web.client.service.common.OperationRequest;

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
