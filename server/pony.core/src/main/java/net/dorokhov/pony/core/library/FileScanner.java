package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.file.LibraryFile;
import net.dorokhov.pony.core.library.file.LibraryFolder;

import java.io.File;

public interface FileScanner {

	public LibraryFile scanFile(File aFile);

	public LibraryFolder scanFolder(File aFolder);

}
