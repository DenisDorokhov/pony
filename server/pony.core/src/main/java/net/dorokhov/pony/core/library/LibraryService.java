package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.entity.Song;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibrarySong;

import java.util.List;

public interface LibraryService {

	public void cleanSongs(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public void cleanArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public Song importSong(List<LibraryFolder> aLibrary, LibrarySong aSongFile);

	public void normalize(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate);

	public Song writeAndImportSong(LibrarySong aSongFile, SongDataWritable aSongData);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}
}
