package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.LibraryFile;
import net.dorokhov.pony.core.common.LibraryFolder;

import java.io.File;

public interface FileScanner {

	public LibraryFile scanFile(File aFile);

	public LibraryFolder scanFolder(File aFolder);

}
