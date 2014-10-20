package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.service.library.LibraryImage;
import net.dorokhov.pony.core.service.library.LibrarySong;

public interface ArtworkDiscoveryService {

	public LibraryImage discoverArtwork(LibrarySong aSong);

}
