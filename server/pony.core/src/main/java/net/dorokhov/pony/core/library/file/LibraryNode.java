package net.dorokhov.pony.core.library.file;

import java.io.File;

public interface LibraryNode {

	public File getFile();

	public LibraryFolder getParentFolder();

}
