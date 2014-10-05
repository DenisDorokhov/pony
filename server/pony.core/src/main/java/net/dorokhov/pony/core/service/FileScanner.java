package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.ScannedFile;
import net.dorokhov.pony.core.common.ScannedFolder;
import net.dorokhov.pony.core.exception.FileNotDirectoryException;

import java.io.File;

public interface FileScanner {

	public ScannedFile scanFile(File aFile) throws FileNotDirectoryException;
	public ScannedFolder scanFolder(File aFolder) throws FileNotDirectoryException;

}
