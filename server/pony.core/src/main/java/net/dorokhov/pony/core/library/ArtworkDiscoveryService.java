package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;

public interface ArtworkDiscoveryService {

	public LibraryImage discoverArtwork(LibrarySong aSong);

}
