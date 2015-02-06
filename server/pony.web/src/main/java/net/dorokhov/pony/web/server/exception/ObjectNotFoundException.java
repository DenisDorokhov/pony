package net.dorokhov.pony.web.server.exception;

public class ObjectNotFoundException extends Exception {

	private Object objectId;

	private String errorCode;

	public ObjectNotFoundException(Object aObjectId) {
		this(aObjectId, "errorObjectNotFound", "Object [" + aObjectId + "] not found.");
	}

	public ObjectNotFoundException(Object aObjectId, String aErrorCode, String aMessage) {

		super(aMessage);

		objectId = aObjectId;
		errorCode = aErrorCode;
	}

	public Object getObjectId() {
		return objectId;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
