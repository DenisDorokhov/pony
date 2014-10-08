package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.FileType;

public interface FileTypeService {

	public String getFileMimeType(String aFileName);
	public String getFileExtension(String aMimeType);

	public FileType getFileType(String aFileName);

}
