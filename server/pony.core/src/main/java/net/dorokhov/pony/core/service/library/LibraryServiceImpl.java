package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.audio.SongDataWritable;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService {

	@Override
	public void cleanSongs() {

	}

	@Override
	public void cleanStoredFiles() {

	}

	@Override
	public ImportResult importSong(LibrarySong aSongFile) {
		return null;
	}

	@Override
	public ImportResult writeAndImportSong(Long aId, SongDataWritable aSongData) {
		return null;
	}

	@Override
	public void importArtworks() {

	}
}
