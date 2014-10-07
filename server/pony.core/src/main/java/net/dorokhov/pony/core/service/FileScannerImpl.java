package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.ScannedFile;
import net.dorokhov.pony.core.common.ScannedFolder;
import net.dorokhov.pony.core.common.dictionary.ImageExtensions;
import net.dorokhov.pony.core.common.dictionary.SongExtensions;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScannerImpl implements FileScanner {

	@Override
	public ScannedFile scanFile(File aFile) {

		if (!aFile.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (aFile.isDirectory()) {
			throw new IllegalArgumentException("File must not be a directory.");
		}

		ScannedFile.Type type = null;

		switch (FilenameUtils.getExtension(aFile.getName())) {

			case SongExtensions.MP3:
				type = ScannedFile.Type.SONG;
				break;

			case ImageExtensions.PNG:
			case ImageExtensions.JPG:
				type = ScannedFile.Type.IMAGE;
				break;
		}

		return type != null ? new ScannedFile(aFile, type) : null;
	}

	@Override
	public ScannedFolder scanFolder(File aFolder) {

		if (!aFolder.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (!aFolder.isDirectory()) {
			throw new IllegalArgumentException("File must be a directory.");
		}

		List<ScannedFile> files = new ArrayList<>();
		List<ScannedFolder> folders = new ArrayList<>();

		File[] fileList = aFolder.listFiles();

		if (fileList != null) {
			for (File childFile : fileList) {
				if (childFile.isDirectory()) {
					folders.add(scanFolder(childFile));
				} else {

					ScannedFile scannedFile = scanFile(childFile);

					if (scannedFile != null) {
						files.add(scannedFile);
					}
				}
			}
		}

		return new ScannedFolder(aFolder, files, folders);
	}
}
