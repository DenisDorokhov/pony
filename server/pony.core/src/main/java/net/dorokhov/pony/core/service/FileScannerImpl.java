package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.FileType;
import net.dorokhov.pony.core.common.LibraryFile;
import net.dorokhov.pony.core.common.LibraryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileScannerImpl implements FileScanner {

	private FileTypeService fileTypeService;

	@Autowired
	public void setFileTypeService(FileTypeService aFileTypeService) {
		fileTypeService = aFileTypeService;
	}

	@Override
	public LibraryFile scanFile(File aFile) {
		return doScanFile(aFile, null);
	}

	@Override
	public LibraryFolder scanFolder(File aFolder) {
		return doScanFolder(aFolder, null);
	}

	private LibraryFile doScanFile(File aFile, LibraryFolder aParentFolder) {

		if (!aFile.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (aFile.isDirectory()) {
			throw new IllegalArgumentException("File must not be a directory.");
		}

		FileType type = fileTypeService.getFileType(aFile.getName());

		return type != null ? new LibraryFile(aFile, type, aParentFolder) : null;
	}

	private LibraryFolder doScanFolder(File aFolder, LibraryFolder aParentFolder) {

		if (!aFolder.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (!aFolder.isDirectory()) {
			throw new IllegalArgumentException("File must be a directory.");
		}

		LibraryFolder currentFolder = new LibraryFolder(aFolder, aParentFolder);

		File[] fileList = aFolder.listFiles();

		if (fileList != null) {
			for (File childFile : fileList) {
				if (childFile.isDirectory()) {
					currentFolder.getChildFolders().add(doScanFolder(childFile, currentFolder));
				} else {

					LibraryFile libraryFile = doScanFile(childFile, currentFolder);

					if (libraryFile != null) {
						currentFolder.getChildFiles().add(libraryFile);
					}
				}
			}
		}

		return currentFolder;
	}
}
