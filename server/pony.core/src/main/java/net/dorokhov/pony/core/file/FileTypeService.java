package net.dorokhov.pony.core.file;

public interface FileTypeService {

	public static enum FileType {
		SONG, IMAGE
	}

	public String getFileMimeType(String aFileName);
	public String getFileExtension(String aMimeType);

	public FileType getFileType(String aFileName);

}
