package net.dorokhov.pony.core.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLConnection;

@Service
public class MimeTypeServiceImpl implements MimeTypeService {

	@Override
	public String getFileMimeType(File aFile) {
		return URLConnection.guessContentTypeFromName(aFile.getName());
	}

	@Override
	public String getFileExtension(String aMimeType) {

		String[] mimeTypeParts = aMimeType.split("/");

		if (mimeTypeParts.length > 1) {
			return mimeTypeParts[1];
		}

		return null;
	}

}
