package net.dorokhov.pony.core.library.exception;

public class AlbumNotFoundException extends Exception {

	private Long albumId;

	public AlbumNotFoundException(Long aAlbumId) {

		super("Album [" + aAlbumId + "] not found.");

		albumId = aAlbumId;
	}

	public Long getAlbumId() {
		return albumId;
	}

}
