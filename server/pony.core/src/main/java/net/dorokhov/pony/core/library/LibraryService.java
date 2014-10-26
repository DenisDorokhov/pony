package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.entity.Song;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibrarySong;

import java.util.List;

public interface LibraryService {

	public void cleanSongs(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public void cleanStoredFiles(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public Song importSong(List<LibraryFolder> aLibrary, LibrarySong aSongFile);

	public Song writeAndImportSong(LibraryFolder aLibrary, Long aId, SongDataWritable aSongData);

	public void importArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}
}
