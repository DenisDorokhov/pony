package net.dorokhov.pony.web.exception;

public class ObjectNotFoundException extends RuntimeException {

	private Long id;

	private String errorCode;

	public ObjectNotFoundException(Long aId) {
		this(aId, "objectNotFound", "Object [" + aId + "] could not be found.");
	}

	public ObjectNotFoundException(Long aId, String aErrorCode, String aMessage) {

		super(aMessage);

		id = aId;
		errorCode = aErrorCode;
	}

	public Long getId() {
		return id;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
