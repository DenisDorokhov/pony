package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.exception.AlbumNotFoundException;
import net.dorokhov.pony.core.library.exception.ArtistNotFoundException;
import net.dorokhov.pony.core.library.exception.SongNotFoundException;
import net.dorokhov.pony.core.library.export.LibraryBatchExportTask;
import net.dorokhov.pony.core.library.export.LibrarySingleExportTask;

public interface LibraryExportTaskService {

	public LibrarySingleExportTask getSongExportTask(Long aSongId) throws SongNotFoundException;

	public LibraryBatchExportTask getArtistExportTask(Long aArtistId) throws ArtistNotFoundException;

	public LibraryBatchExportTask getAlbumExportTask(Long aAlbumId) throws AlbumNotFoundException;

}
