package net.dorokhov.pony.web.domain;

import java.util.ArrayList;
import java.util.List;

public class ResponseDto<T> {

	private String version;

	private boolean successful;

	private List<ErrorDto> errors;

	private T data;

	public String getVersion() {
		return version;
	}

	public void setVersion(String aVersion) {
		version = aVersion;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean aSuccessful) {
		successful = aSuccessful;
	}

	public List<ErrorDto> getErrors() {

		if (errors == null) {
			errors = new ArrayList<>();
		}

		return errors;
	}

	public void setErrors(List<ErrorDto> aErrors) {
		errors = aErrors;
	}

	public T getData() {
		return data;
	}

	public void setData(T aData) {
		data = aData;
	}
}
