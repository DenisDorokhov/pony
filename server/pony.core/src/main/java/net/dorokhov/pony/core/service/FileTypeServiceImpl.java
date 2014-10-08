package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.FileType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class FileTypeServiceImpl implements FileTypeService {

	private final Map<String, String> extensionToMimeType;
	private final Map<String, String> mimeTypeToExtension;

	private final Set<String> imageExtensions;
	private final Set<String> songExtensions;

	public FileTypeServiceImpl() {

		extensionToMimeType = new HashMap<>();
		extensionToMimeType.put("jpg", "image/jpeg");
		extensionToMimeType.put("jpeg", "image/jpeg");
		extensionToMimeType.put("png", "image/png");
		extensionToMimeType.put("mp3", "audio/mpeg3");

		mimeTypeToExtension = new HashMap<>();
		mimeTypeToExtension.put("image/jpeg", "jpg");
		mimeTypeToExtension.put("image/png", "png");
		mimeTypeToExtension.put("audio/mpeg3", "mp3");

		imageExtensions = new HashSet<>();
		imageExtensions.add("jpg");
		imageExtensions.add("jpeg");
		imageExtensions.add("png");

		songExtensions = new HashSet<>();
		songExtensions.add("mp3");
	}

	@Override
	public String getFileMimeType(File aFile) {
		return extensionToMimeType.get(FilenameUtils.getExtension(aFile.getName()));
	}

	@Override
	public String getFileExtension(String aMimeType) {
		return mimeTypeToExtension.get(aMimeType);
	}

	@Override
	public FileType getFileType(File aFile) {

		FileType type = null;

		if (imageExtensions.contains(FilenameUtils.getExtension(aFile.getName()))) {
			type = FileType.IMAGE;
		} else if (songExtensions.contains(FilenameUtils.getExtension(aFile.getName()))) {
			type = FileType.SONG;
		}

		return type;
	}
}
