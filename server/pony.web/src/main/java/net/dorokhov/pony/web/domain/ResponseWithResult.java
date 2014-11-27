package net.dorokhov.pony.web.domain;

import java.io.Serializable;

public class ResponseWithResult<T> extends Response implements Serializable {

	private final T result;

	public ResponseWithResult() {

		super(false);

		result = null;
	}

	public ResponseWithResult(T aResult) {

		super(true);

		result = aResult;
	}

	public T getResult() {
		return result;
	}
}
