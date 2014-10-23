package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.service.audio.SongDataWritable;
import net.dorokhov.pony.core.service.library.common.LibraryFolder;
import net.dorokhov.pony.core.service.library.common.LibrarySong;

import java.util.List;

public interface LibraryService {

	public void cleanSongs(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public void cleanArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public Song importSong(List<LibraryFolder> aLibrary, LibrarySong aSongFile);

	public Song writeAndImportSong(LibraryFolder aLibrary, Long aId, SongDataWritable aSongData);

	public void importArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}
}
