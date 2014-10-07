package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.ScannedFile;
import net.dorokhov.pony.core.common.ScannedFolder;

import java.io.File;

public interface FileScanner {

	public ScannedFile scanFile(File aFile);
	public ScannedFolder scanFolder(File aFolder);

}
