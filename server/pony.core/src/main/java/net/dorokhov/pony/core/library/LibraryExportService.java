package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.exception.AlbumNotFoundException;
import net.dorokhov.pony.core.library.exception.ArtistNotFoundException;
import net.dorokhov.pony.core.library.exception.FileNotFoundException;
import net.dorokhov.pony.core.library.exception.SongNotFoundException;

import java.io.OutputStream;

public interface LibraryExportService {

	public ExportDescriptor exportArtistSongs(Long aId, OutputStream aOutputStream) throws ArtistNotFoundException, FileNotFoundException;

	public ExportDescriptor exportAlbumSongs(Long aId, OutputStream aOutputStream) throws AlbumNotFoundException, FileNotFoundException;

	public ExportDescriptor exportSong(Long aId, OutputStream aOutputStream) throws SongNotFoundException, FileNotFoundException;

	public static interface ExportDescriptor {

		public String getMimeType();

		public String getFileName();

	}

}
