package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.library.exception.FileNotFoundException;

import java.io.File;
import java.util.List;

public interface LibraryExportService {

	public File exportSong(Song aSong, File aFolder) throws FileNotFoundException;

	public List<File> exportSongList(List<Song> aSongList, File aFolder) throws FileNotFoundException;

}
