package net.dorokhov.pony.core.service.library;

import java.io.File;

public interface FileScanner {

	public LibraryFile scanFile(File aFile);

	public LibraryFolder scanFolder(File aFolder);

}
