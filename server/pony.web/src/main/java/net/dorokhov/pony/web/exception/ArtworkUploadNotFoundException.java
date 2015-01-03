package net.dorokhov.pony.web.exception;

public class ArtworkUploadNotFoundException extends Exception {

	private Long artworkUploadId;

	public ArtworkUploadNotFoundException(Long aArtworkUploadId) {

		super("Artwork upload [" + aArtworkUploadId + "] not found.");

		artworkUploadId = aArtworkUploadId;
	}

	public Long getArtworkUploadId() {
		return artworkUploadId;
	}
}
