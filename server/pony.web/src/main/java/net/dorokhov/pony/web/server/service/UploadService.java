package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.shared.ArtworkUploadDto;
import net.dorokhov.pony.web.server.exception.ArtworkUploadFormatException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UploadService {

	public ArtworkUploadDto uploadArtwork(MultipartFile aMultipartFile) throws ArtworkUploadFormatException;

	public ArtworkUploadDto getArtworkUpload(Long aId) throws ArtworkUploadNotFoundException;

	public File getArtworkUploadFile(Long aId) throws ArtworkUploadNotFoundException;

	public void cleanUploads();
}
