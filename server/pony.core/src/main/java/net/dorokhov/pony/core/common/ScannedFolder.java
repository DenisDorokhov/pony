package net.dorokhov.pony.core.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannedFolder {

	private final File file;

	private final List<ScannedFolder> childFolders;
	private final List<ScannedFile> childFiles;

	public ScannedFolder(File aFile) {
		this(aFile, null, null);
	}

	public ScannedFolder(File aFile, List<ScannedFile> aChildFiles, List<ScannedFolder> aChildFolders) {

		if (aFile == null) {
			throw new NullPointerException();
		}

		file = aFile;

		childFolders = aChildFolders != null ? aChildFolders : new ArrayList<ScannedFolder>();
		childFiles = aChildFiles != null ? aChildFiles : new ArrayList<ScannedFile>();
	}

	public File getFile() {
		return file;
	}

	public List<ScannedFolder> getChildFolders() {
		return new ArrayList<>(childFolders);
	}

	public List<ScannedFile> getChildFiles() {
		return new ArrayList<>(childFiles);
	}

	public ScannedFile getFileByName(String aName) {

		for (ScannedFile file : childFiles) {
			if (file.getFile().getName().equals(aName)) {
				return file;
			}
		}

		return null;
	}

	public ScannedFolder getFolderByName(String aName) {

		for (ScannedFolder folder : childFolders) {
			if (folder.getFile().getName().equals(aName)) {
				return folder;
			}
		}

		return null;
	}

	public List<ScannedFile> getImageFiles(boolean aRecursive) {

		List<ScannedFile> result = new ArrayList<>();

		doGetImageFiles(result, aRecursive);

		return result;
	}

	public List<ScannedFile> getSongFiles(boolean aRecursive) {

		List<ScannedFile> result = new ArrayList<>();

		doGetSongFiles(result, aRecursive);

		return result;
	}

	private void doGetImageFiles(List<ScannedFile> aResult, boolean aRecursive) {

		for (ScannedFile file : childFiles) {
			if (file.getType() == ScannedFile.Type.IMAGE) {
				aResult.add(file);
			}
		}

		if (aRecursive) {
			for (ScannedFolder folder : childFolders) {
				folder.doGetImageFiles(aResult, true);
			}
		}
	}

	private void doGetSongFiles(List<ScannedFile> aResult, boolean aRecursive) {

		for (ScannedFile file : childFiles) {
			if (file.getType() == ScannedFile.Type.SONG) {
				aResult.add(file);
			}
		}

		if (aRecursive) {
			for (ScannedFolder folder : childFolders) {
				folder.doGetSongFiles(aResult, true);
			}
		}
	}
}
