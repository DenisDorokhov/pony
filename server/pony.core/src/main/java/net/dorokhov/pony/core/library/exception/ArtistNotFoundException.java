package net.dorokhov.pony.core.library.exception;

public class ArtistNotFoundException extends Exception {

	private Long artistId;

	public ArtistNotFoundException(Long aArtistId) {

		super("Artist [" + aArtistId + "] not found.");

		artistId = aArtistId;
	}

	public Long getArtistId() {
		return artistId;
	}

}
