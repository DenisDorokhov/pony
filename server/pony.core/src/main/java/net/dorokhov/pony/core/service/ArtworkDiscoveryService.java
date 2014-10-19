package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.LibraryImage;
import net.dorokhov.pony.core.common.LibrarySong;

public interface ArtworkDiscoveryService {

	public LibraryImage discoverArtwork(LibrarySong aSong);

}
