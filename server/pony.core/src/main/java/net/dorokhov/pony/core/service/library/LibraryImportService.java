package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.service.audio.SongDataWritable;

public interface LibraryImportService {

	public ImportResult importSong(LibrarySong aSongFile);
	public ImportResult importSong(Long aId, SongDataWritable aSongData);

	public static interface ImportResult {

		public static enum Status {
			UNCHANGED, CREATED, MODIFIED
		}

		public Song getSong();

		public Status getStatus();

		public boolean isArtistCreated();
		public boolean isArtistDeleted();

		public boolean isAlbumCreated();
		public boolean isAlbumDeleted();

		public boolean isGenreCreated();
		public boolean isGenreDeleted();

		public boolean isArtworkCreated();
		public boolean isArtworkDeleted();

	}
}
