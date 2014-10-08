package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.FileType;

import java.io.File;

public interface FileTypeService {

	public String getFileMimeType(File aFile);
	public String getFileExtension(String aMimeType);

	public FileType getFileType(File aFile);

}
