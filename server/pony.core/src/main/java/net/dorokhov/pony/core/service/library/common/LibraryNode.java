package net.dorokhov.pony.core.service.library.common;

import java.io.File;

public interface LibraryNode {

	public File getFile();

	public LibraryFolder getParentFolder();

}
