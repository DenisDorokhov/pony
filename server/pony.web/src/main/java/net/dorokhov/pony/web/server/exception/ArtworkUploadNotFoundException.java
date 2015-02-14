package net.dorokhov.pony.web.server.exception;

import net.dorokhov.pony.web.shared.ErrorCode;

public class ArtworkUploadNotFoundException extends ObjectNotFoundException {

	public ArtworkUploadNotFoundException(Long aArtworkUploadId) {
		super(aArtworkUploadId, ErrorCode.ARTWORK_UPLOAD_NOT_FOUND, "Artwork upload [" + aArtworkUploadId + "] not found.");
	}

}
