package net.dorokhov.pony.web.exception;

public class ArtworkUploadNotFoundException extends ObjectNotFoundException {

	public ArtworkUploadNotFoundException(Long aArtworkUploadId) {
		super(aArtworkUploadId, "errorArtworkUploadNotFound", "Artwork upload [" + aArtworkUploadId + "] not found.");
	}

}
