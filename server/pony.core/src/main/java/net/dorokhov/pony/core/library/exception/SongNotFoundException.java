package net.dorokhov.pony.core.library.exception;

public class SongNotFoundException extends RuntimeException {

	private Long songId;

	public SongNotFoundException(Long aSongId) {

		super("Song '" + aSongId + "' not found.");

		songId = aSongId;
	}

	public Long getSongId() {
		return songId;
	}
}
