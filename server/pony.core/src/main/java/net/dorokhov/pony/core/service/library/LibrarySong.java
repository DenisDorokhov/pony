package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.audio.SongData;

public interface LibrarySong extends LibraryFile {

	public SongData getData() throws Exception;

}
