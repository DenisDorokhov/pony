package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.library.common.LibraryImage;
import net.dorokhov.pony.core.service.library.common.LibrarySong;

public interface ArtworkDiscoveryService {

	public LibraryImage discoverArtwork(LibrarySong aSong);

}
