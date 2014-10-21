package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.service.audio.SongDataWritable;
import net.dorokhov.pony.core.service.library.common.LibraryFolder;
import net.dorokhov.pony.core.service.library.common.LibrarySong;

public interface LibraryService {

	public long cleanSongs(LibraryFolder aLibrary, ProgressDelegate aDelegate);

	public long cleanArtworks(ProgressDelegate aDelegate);

	public SongImportResult importSong(LibrarySong aSongFile);

	public SongImportResult writeAndImportSong(Long aId, SongDataWritable aSongData);

	public long importArtworks(ProgressDelegate aDelegate);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}

	public static interface ImportResult {


	}

	public static interface SongImportResult {

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
