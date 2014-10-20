package net.dorokhov.pony.core.service.library;

import java.util.Set;

public interface LibraryFolder extends LibraryNode {

	public Set<LibraryImage> getChildImages();
	public Set<LibraryImage> getChildImages(boolean aRecursive);

	public Set<LibrarySong> getChildSongs();
	public Set<LibrarySong> getChildSongs(boolean aRecursive);

	public Set<LibraryFolder> getChildFolders();
	public Set<LibraryFolder> getChildFolders(boolean aRecursive);

	public Set<LibraryFile> getChildFiles();
	public Set<LibraryFile> getChildFiles(boolean aRecursive);

}
