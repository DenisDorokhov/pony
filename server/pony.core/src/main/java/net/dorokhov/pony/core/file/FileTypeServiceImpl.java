package net.dorokhov.pony.core.file;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

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
	public String getFileMimeType(String aFileName) {

		String mimeType = null;

		if (!aFileName.trim().startsWith(".")) {
			mimeType = extensionToMimeType.get(FilenameUtils.getExtension(aFileName).toLowerCase());
		}

		return mimeType;
	}

	@Override
	public String getFileExtension(String aMimeType) {
		return mimeTypeToExtension.get(aMimeType);
	}

	@Override
	public FileType getFileType(String aFileName) {

		FileType type = null;

		if (!aFileName.trim().startsWith(".")) {

			String extension = FilenameUtils.getExtension(aFileName).toLowerCase();

			if (imageExtensions.contains(extension)) {
				type = FileType.IMAGE;
			} else if (songExtensions.contains(extension)) {
				type = FileType.SONG;
			}
		}

		return type;
	}
}
