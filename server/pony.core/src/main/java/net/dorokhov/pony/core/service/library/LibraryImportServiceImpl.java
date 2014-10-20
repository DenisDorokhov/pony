package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.audio.SongDataWritable;
import org.springframework.stereotype.Service;

@Service
public class LibraryImportServiceImpl implements LibraryImportService {

	@Override
	public ImportResult importSong(LibrarySong aSongFile) {
		return null;
	}

	@Override
	public ImportResult importSong(Long aId, SongDataWritable aSongData) {
		return null;
	}

}
