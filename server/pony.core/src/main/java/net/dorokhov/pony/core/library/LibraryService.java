package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.entity.Song;
import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;

import java.util.List;

public interface LibraryService {

	public void cleanSongs(List<LibrarySong> aSongFiles, ProgressDelegate aDelegate);

	public void cleanArtworks(List<LibraryImage> aImageFiles, ProgressDelegate aDelegate);

	public void normalize(ProgressDelegate aDelegate);

	public Song importSong(LibrarySong aSongFile);

	public Song writeAndImportSong(LibrarySong aSongFile, SongDataWritable aSongData);

	public static interface ProgressDelegate {

		public void onProgress(double aProgress);

	}
}
