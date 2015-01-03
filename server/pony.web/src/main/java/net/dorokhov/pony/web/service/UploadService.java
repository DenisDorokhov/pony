package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ArtworkUploadDto;
import net.dorokhov.pony.web.exception.ArtworkUploadFormatException;
import net.dorokhov.pony.web.exception.ArtworkUploadNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UploadService {

	public ArtworkUploadDto uploadArtwork(MultipartFile aMultipartFile) throws ArtworkUploadFormatException;

	public ArtworkUploadDto getArtworkUpload(Long aId) throws ArtworkUploadNotFoundException;

	public File getArtworkUploadFile(Long aId) throws ArtworkUploadNotFoundException;

	public void cleanUploads();
}
