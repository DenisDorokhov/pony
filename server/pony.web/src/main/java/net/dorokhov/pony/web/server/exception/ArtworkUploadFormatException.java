package net.dorokhov.pony.web.server.exception;

public class ArtworkUploadFormatException extends Exception {

	public ArtworkUploadFormatException() {
		super("Format of uploaded artwork is not supported.");
	}

}
