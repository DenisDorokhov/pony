package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.library.common.LibraryFile;
import net.dorokhov.pony.core.service.library.common.LibraryFolder;

import java.io.File;

public interface FileScanner {

	public LibraryFile scanFile(File aFile);

	public LibraryFolder scanFolder(File aFolder);

}
