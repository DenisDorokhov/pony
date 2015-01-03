package net.dorokhov.pony.web.exception;

public class ArtworkUploadFormatException extends Exception {

	public ArtworkUploadFormatException() {
		super("Format of uploaded artwork is not supported.");
	}

}
