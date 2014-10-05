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

	public List<ScannedFile> getImageFiles() {

		List<ScannedFile> result = new ArrayList<>();

		for (ScannedFile file : childFiles) {
			if (file.getType() == ScannedFile.Type.IMAGE) {
				result.add(file);
			}
		}

		return result;
	}

	public List<ScannedFile> getSongFiles() {

		List<ScannedFile> result = new ArrayList<>();

		for (ScannedFile file : childFiles) {
			if (file.getType() == ScannedFile.Type.SONG) {
				result.add(file);
			}
		}

		return result;
	}
}
