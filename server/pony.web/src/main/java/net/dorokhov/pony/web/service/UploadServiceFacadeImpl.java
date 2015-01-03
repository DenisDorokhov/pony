package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.common.PageProcessor;
import net.dorokhov.pony.core.common.SimpleImageInfo;
import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.file.ChecksumService;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.storage.StoreFileCommand;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.domain.ArtworkUploadDto;
import net.dorokhov.pony.web.exception.ArtworkUploadFormatException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UploadServiceFacadeImpl implements UploadServiceFacade {

	private static final String TAG_ARTWORK_UPLOAD = "artworkUpload";

	private static final int CLEANING_BUFFER_SIZE = 300;

	private static final long ARTWORK_UPLOAD_LIFETIME = 24 * 60 * 60;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private InstallationService installationService;

	private StoredFileService storedFileService;

	private ChecksumService checksumService;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Autowired
	public void setChecksumService(ChecksumService aChecksumService) {
		checksumService = aChecksumService;
	}

	@Override
	@Transactional
	public ArtworkUploadDto uploadArtwork(MultipartFile aMultipartFile) throws ArtworkUploadFormatException {

		File file;

		try {
			file = moveMultipartFile(aMultipartFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String checksum;

		try {
			checksum = checksumService.calculateChecksum(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		StoredFile artwork = storedFileService.getByTagAndChecksum(TAG_ARTWORK_UPLOAD, checksum);

		if (artwork == null) {

			SimpleImageInfo imageInfo;

			try {
				imageInfo = new SimpleImageInfo(file);
			} catch (IOException e) {

				log.warn("Could not get image info.", e);

				throw new ArtworkUploadFormatException();
			}

			StoreFileCommand command = new StoreFileCommand(StoreFileCommand.Type.MOVE, file);

			command.setName(file.getName());
			command.setMimeType(imageInfo.getMimeType());
			command.setChecksum(checksum);
			command.setTag(TAG_ARTWORK_UPLOAD);

			artwork = storedFileService.save(command);

			log.debug("Storing uploaded artwork " + artwork + ".");
		}

		return ArtworkUploadDto.valueOf(artwork);
	}

	@Override
	@Transactional(readOnly = true)
	public ArtworkUploadDto getArtworkUpload(Long aId) {

		StoredFile storedFile = storedFileService.getById(aId);

		if (storedFile == null || !Objects.equals(storedFile.getTag(), TAG_ARTWORK_UPLOAD)) {
			return null;
		}

		return ArtworkUploadDto.valueOf(storedFile);
	}

	@Override
	@Transactional(readOnly = true)
	public File getArtworkUploadFile(Long aId) {

		StoredFile storedFile = storedFileService.getById(aId);

		if (storedFile == null || !Objects.equals(storedFile.getTag(), TAG_ARTWORK_UPLOAD)) {
			return null;
		}

		return storedFileService.getFile(storedFile);
	}

	@Override
	@Transactional
	@Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
	public void cleanUploads() {
		if (installationService.getInstallation() != null) {

			log.debug("Cleaning uploads...");

			doCleanUploads();
		}
	}

	private void doCleanUploads() {

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<StoredFile> handler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				long storedFileAge = (new Date().getTime() - aStoredFile.getDate().getTime()) / 1000;

				if (storedFileAge >= ARTWORK_UPLOAD_LIFETIME) {
					itemsToDelete.add(aStoredFile.getId());
				}
			}

			@Override
			public Page<StoredFile> getPage(Pageable aPageable) {
				return storedFileService.getByTag(TAG_ARTWORK_UPLOAD, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();

		for (final Long id : itemsToDelete) {

			log.debug("Deleting artwork upload [" + id + "].");

			storedFileService.delete(id);
		}
	}

	private File moveMultipartFile(MultipartFile aMultipartFile) throws Exception {

		String extension = ".upload";

		if (aMultipartFile.getOriginalFilename() != null) {
			extension = "." + FilenameUtils.getExtension(aMultipartFile.getOriginalFilename());
		}

		File file = File.createTempFile("multipart", extension);

		aMultipartFile.transferTo(file);

		return file;
	}
}
