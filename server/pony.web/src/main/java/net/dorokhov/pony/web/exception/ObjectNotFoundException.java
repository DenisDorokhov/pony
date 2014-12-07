package net.dorokhov.pony.web.exception;

public class ObjectNotFoundException extends Exception {

	private Object id;

	private String errorCode;

	public ObjectNotFoundException(Object aId) {
		this(aId, "objectNotFound", "Object [" + aId + "] could not be found.");
	}

	public ObjectNotFoundException(Object aId, String aErrorCode, String aMessage) {

		super(aMessage);

		id = aId;
		errorCode = aErrorCode;
	}

	public Object getId() {
		return id;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
