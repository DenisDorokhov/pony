package net.dorokhov.pony.core.service;

import java.io.File;

public interface MimeTypeService {

	public String getFileMimeType(File aFile);

	public String getFileExtension(String aMimeType);

}
