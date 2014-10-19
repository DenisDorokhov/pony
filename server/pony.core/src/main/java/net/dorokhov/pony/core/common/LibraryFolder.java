package net.dorokhov.pony.core.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibraryFolder {

	private final File file;

	private LibraryFolder parentFolder;

	private List<LibraryFolder> childFolders;
	private List<LibraryFile> childFiles;

	public LibraryFolder(File aFile, LibraryFolder aParentFolder) {

		if (aFile == null) {
			throw new NullPointerException("File cannot be null.");
		}

		file = aFile;

		setParentFolder(aParentFolder);
	}

	public File getFile() {
		return file;
	}

	public LibraryFolder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(LibraryFolder aParentFolder) {
		parentFolder = aParentFolder;
	}

	public List<LibraryFolder> getChildFolders() {

		if (childFolders == null) {
			childFolders = new ArrayList<>();
		}

		return childFolders;
	}

	public void setChildFolders(List<LibraryFolder> aChildFolders) {
		childFolders = aChildFolders;
	}

	public List<LibraryFile> getChildFiles() {

		if (childFiles == null) {
			childFiles = new ArrayList<>();
		}

		return childFiles;
	}

	public void setChildFiles(List<LibraryFile> aChildFiles) {
		childFiles = aChildFiles;
	}

	public LibraryFile getChildFileByName(String aName) {

		for (LibraryFile file : getChildFiles()) {
			if (file.getFile().getName().equals(aName)) {
				return file;
			}
		}

		return null;
	}

	public LibraryFolder getChildFolderByName(String aName) {

		for (LibraryFolder folder : getChildFolders()) {
			if (folder.getFile().getName().equals(aName)) {
				return folder;
			}
		}

		return null;
	}

	public List<LibraryFile> getImageFiles(boolean aRecursive) {

		List<LibraryFile> result = new ArrayList<>();

		doGetImageFiles(result, aRecursive);

		return result;
	}

	public List<LibraryFile> getSongFiles(boolean aRecursive) {

		List<LibraryFile> result = new ArrayList<>();

		doGetSongFiles(result, aRecursive);

		return result;
	}

	private void doGetImageFiles(List<LibraryFile> aResult, boolean aRecursive) {

		for (LibraryFile file : getChildFiles()) {
			if (file.getType() == FileType.IMAGE) {
				aResult.add(file);
			}
		}

		if (aRecursive) {
			for (LibraryFolder folder : getChildFolders()) {
				folder.doGetImageFiles(aResult, true);
			}
		}
	}

	private void doGetSongFiles(List<LibraryFile> aResult, boolean aRecursive) {

		for (LibraryFile file : getChildFiles()) {
			if (file.getType() == FileType.SONG) {
				aResult.add(file);
			}
		}

		if (aRecursive) {
			for (LibraryFolder folder : getChildFolders()) {
				folder.doGetSongFiles(aResult, true);
			}
		}
	}
}
