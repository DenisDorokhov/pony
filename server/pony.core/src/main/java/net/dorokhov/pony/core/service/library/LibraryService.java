package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.service.audio.SongDataWritable;

public interface LibraryService {

	public void cleanSongs(LibraryFolder aLibrary, ProgressDelegate aDelegate);

	public void cleanStoredFiles(ProgressDelegate aDelegate);

	public ImportResult importSong(LibrarySong aSongFile);

	public ImportResult writeAndImportSong(Long aId, SongDataWritable aSongData);

	public void importArtworks(ProgressDelegate aDelegate);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}

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
