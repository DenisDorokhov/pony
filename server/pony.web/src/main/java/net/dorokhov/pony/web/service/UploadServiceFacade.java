package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ArtworkUploadDto;
import net.dorokhov.pony.web.exception.ArtworkUploadFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UploadServiceFacade {

	public ArtworkUploadDto uploadArtwork(MultipartFile aMultipartFile) throws ArtworkUploadFormatException;

	public ArtworkUploadDto getArtworkUpload(Long aId);

	public File getArtworkUploadFile(Long aId);

	public void cleanUploads();
}
