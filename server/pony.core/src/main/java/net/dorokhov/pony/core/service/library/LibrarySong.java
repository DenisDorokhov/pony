package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.common.SongData;

public interface LibrarySong extends LibraryFile {

	public SongData getData() throws Exception;

}
