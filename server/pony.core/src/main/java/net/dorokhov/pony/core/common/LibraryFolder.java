package net.dorokhov.pony.core.common;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibraryFolder extends LibraryNode {

	private final Set<LibraryImage> childImages;
	private final Set<LibrarySong> childSongs;
	private final Set<LibraryFolder> childFolders;

	private final Map<String, LibraryImage> fileNameToChildImage;
	private final Map<String, LibrarySong> fileNameToChildSong;
	private final Map<String, LibraryFolder> fileNameToChildFolder;

	public LibraryFolder(File aFile) {
		this(aFile, null);
	}

	public LibraryFolder(File aFile, LibraryFolder aParentFolder) {

		super(aFile, aParentFolder);

		childImages = new HashSet<>();
		childSongs = new HashSet<>();
		childFolders = new HashSet<>();

		fileNameToChildImage = new HashMap<>();
		fileNameToChildSong = new HashMap<>();
		fileNameToChildFolder = new HashMap<>();
	}

	public Set<LibraryImage> getChildImages() {
		return new HashSet<>(childImages);
	}

	public Set<LibraryImage> getChildImages(boolean aRecursive) {

		Set<LibraryImage> result = new HashSet<>();

		doGetChildImages(result, aRecursive);

		return result;
	}

	public void addChildImage(LibraryImage aImage) {
		childImages.add(aImage);
		fileNameToChildImage.put(aImage.getFile().getName(), aImage);
	}

	public void removeChildImage(LibraryImage aImage) {
		childImages.remove(aImage);
		fileNameToChildImage.remove(aImage.getFile().getName());
	}

	public Set<LibrarySong> getChildSongs() {
		return new HashSet<>(childSongs);
	}

	public Set<LibrarySong> getChildSongs(boolean aRecursive) {

		Set<LibrarySong> result = new HashSet<>();

		doGetChildSongs(result, aRecursive);

		return result;
	}

	public void addChildSong(LibrarySong aSong) {
		childSongs.add(aSong);
		fileNameToChildSong.put(aSong.getFile().getName(), aSong);
	}

	public void removeChildSong(LibrarySong aSong) {
		childSongs.remove(aSong);
		fileNameToChildSong.remove(aSong.getFile().getName());
	}

	public Set<LibraryFolder> getChildFolders() {
		return new HashSet<>(childFolders);
	}

	public void addChildFolder(LibraryFolder aFolder) {
		childFolders.add(aFolder);
		fileNameToChildFolder.put(aFolder.getFile().getName(), aFolder);
	}

	public void removeChildFolder(LibraryFolder aFolder) {
		childFolders.remove(aFolder);
		fileNameToChildFolder.remove(aFolder.getFile().getName());
	}

	public Set<LibraryFile> getChildFiles() {
		return getChildFiles(false);
	}

	public Set<LibraryFile> getChildFiles(boolean aRecursive) {

		Set<LibraryFile> result = new HashSet<>();

		doGetChildFiles(result, aRecursive);

		return result;
	}

	public void addChildFile(LibraryFile aFile) {
		if (aFile instanceof LibraryImage) {
			addChildImage((LibraryImage)aFile);
		} else if (aFile instanceof LibrarySong) {
			addChildSong((LibrarySong)aFile);
		} else {
			throw new IllegalArgumentException("Unknown file type.");
		}
	}

	public void removeChildFile(LibraryFile aFile) {
		if (aFile instanceof LibraryImage) {
			removeChildImage((LibraryImage)aFile);
		} else if (aFile instanceof LibrarySong) {
			removeChildSong((LibrarySong)aFile);
		} else {
			throw new IllegalArgumentException("Unknown file type.");
		}
	}

	public LibraryImage getChildImageByName(String aName) {
		return fileNameToChildImage.get(aName);
	}

	public LibrarySong getChildSongByName(String aName) {
		return fileNameToChildSong.get(aName);
	}

	public LibraryFile getChildFileByName(String aName) {

		LibraryFile result = getChildImageByName(aName);

		if (result == null) {
			result = getChildSongByName(aName);
		}

		return result;
	}

	public LibraryFolder getChildFolderByName(String aName) {
		return fileNameToChildFolder.get(aName);
	}

	private void doGetChildImages(Set<LibraryImage> aResult, boolean aRecursive) {

		for (LibraryImage image : getChildImages()) {
			aResult.add(image);
		}

		if (aRecursive) {
			for (LibraryFolder folder : getChildFolders()) {
				folder.doGetChildImages(aResult, true);
			}
		}
	}

	private void doGetChildSongs(Set<LibrarySong> aResult, boolean aRecursive) {

		for (LibrarySong song : getChildSongs()) {
			aResult.add(song);
		}

		if (aRecursive) {
			for (LibraryFolder folder : getChildFolders()) {
				folder.doGetChildSongs(aResult, true);
			}
		}
	}

	private void doGetChildFiles(Set<LibraryFile> aResult, boolean aRecursive) {

		aResult.addAll(getChildImages());
		aResult.addAll(getChildSongs());

		if (aRecursive) {
			for (LibraryFolder folder : getChildFolders()) {
				folder.doGetChildFiles(aResult, true);
			}
		}
	}

}
