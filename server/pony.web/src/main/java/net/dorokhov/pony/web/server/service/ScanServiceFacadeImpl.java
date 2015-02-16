package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.library.ScanEditCommand;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.*;
import net.dorokhov.pony.web.shared.command.ScanEditCommandDto;
import net.dorokhov.pony.web.shared.list.ScanJobListDto;
import net.dorokhov.pony.web.shared.list.ScanResultListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScanServiceFacadeImpl implements ScanServiceFacade {

	private static final int MAX_PAGE_SIZE = 100;

	private ScanJobService scanJobService;

	private ScanService scanService;

	private UploadService uploadService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setScanJobService(ScanJobService aScanJobService) {
		scanJobService = aScanJobService;
	}

	@Autowired
	public void setScanService(ScanService aScanService) {
		scanService = aScanService;
	}

	@Autowired
	public void setUploadService(UploadService aUploadService) {
		uploadService = aUploadService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional
	public ScanJobDto startScanJob() throws LibraryNotDefinedException {
		return dtoConverter.scanJobToDto(scanJobService.startScanJob());
	}

	@Override
	@Transactional
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException {

		List<ScanEditCommand> commandList = new ArrayList<>();

		for (ScanEditCommandDto.Task task : aCommand.getTasks()) {

			ScanEditCommand command = new ScanEditCommand();

			command.setSongId(task.getSongId());
			command.setSongData(new SongDataWritable());

			command.getSongData().setDiscNumber(task.getDiscNumber());
			command.getSongData().setDiscCount(task.getDiscCount());
			command.getSongData().setTrackNumber(task.getTrackNumber());
			command.getSongData().setTrackCount(task.getTrackCount());
			command.getSongData().setTitle(task.getTitle());
			command.getSongData().setArtist(task.getArtist());
			command.getSongData().setAlbumArtist(task.getAlbumArtist());
			command.getSongData().setAlbum(task.getAlbum());
			command.getSongData().setYear(task.getYear());
			command.getSongData().setGenre(task.getGenre());

			command.getSongData().setWriteDiscNumber(task.getWriteDiscNumber());
			command.getSongData().setWriteDiscCount(task.getWriteDiscCount());
			command.getSongData().setWriteTrackNumber(task.getWriteTrackNumber());
			command.getSongData().setWriteTrackCount(task.getWriteTrackCount());
			command.getSongData().setWriteTitle(task.getWriteTitle());
			command.getSongData().setWriteArtist(task.getWriteArtist());
			command.getSongData().setWriteAlbumArtist(task.getWriteAlbumArtist());
			command.getSongData().setWriteAlbum(task.getWriteAlbum());
			command.getSongData().setWriteYear(task.getWriteYear());
			command.getSongData().setWriteGenre(task.getWriteGenre());
			command.getSongData().setWriteArtwork(task.getWriteArtwork());

			if (task.getArtworkUploadId() != null) {

				File artwork;

				try {
					artwork = uploadService.getArtworkUploadFile(task.getArtworkUploadId());
				} catch (ObjectNotFoundException e) {
					throw new ArtworkUploadNotFoundException(task.getArtworkUploadId());
				}

				command.getSongData().setArtwork(artwork);
			}
		}

		return dtoConverter.scanJobToDto(scanJobService.startEditJob(commandList));
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobListDto getScanJobs(int aPageNumber, int aPageSize) throws InvalidArgumentException {

		if (aPageNumber < 0) {
			throw new InvalidArgumentException(ErrorCode.PAGE_NUMBER_INVALID, "Page number [" + aPageNumber + "] is invalid.", String.valueOf(aPageNumber));
		}
		if (aPageSize > MAX_PAGE_SIZE) {
			throw new InvalidArgumentException(ErrorCode.PAGE_SIZE_INVALID, "Page size [" + aPageNumber + "] must be less than or equal to [" + MAX_PAGE_SIZE + "]",
					String.valueOf(aPageSize), String.valueOf(MAX_PAGE_SIZE));
		}

		Page<ScanJob> page = scanJobService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "updateDate"));

		return dtoConverter.pagedListToDto(ScanJobListDto.class, page, new DtoConverter.ListConverter<ScanJob, ScanJobDto>() {
			@Override
			public ScanJobDto convert(ScanJob aItem) {
				return dtoConverter.scanJobToDto(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException {

		ScanJob job = scanJobService.getById(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, ErrorCode.SCAN_JOB_NOT_FOUND, "Scan job [" + aId + "] not found.");
		}

		return dtoConverter.scanJobToDto(job);
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResultListDto getScanResults(int aPageNumber, int aPageSize) throws InvalidArgumentException {

		if (aPageNumber < 0) {
			throw new InvalidArgumentException(ErrorCode.PAGE_NUMBER_INVALID, "Page number [" + aPageNumber + "] is invalid", String.valueOf(aPageNumber));
		}
		if (aPageSize > MAX_PAGE_SIZE) {
			throw new InvalidArgumentException(ErrorCode.PAGE_SIZE_INVALID, "Page size [" + aPageNumber + "] must be less than or equal to [" + MAX_PAGE_SIZE + "]",
					String.valueOf(aPageSize), String.valueOf(MAX_PAGE_SIZE));
		}

		Page<ScanResult> page = scanService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date"));

		return dtoConverter.pagedListToDto(ScanResultListDto.class, page, new DtoConverter.ListConverter<ScanResult, ScanResultDto>() {
			@Override
			public ScanResultDto convert(ScanResult aItem) {
				return dtoConverter.scanResultToDto(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException {

		ScanResult result = scanService.getById(aId);

		if (result == null) {
			throw new ObjectNotFoundException(aId, ErrorCode.SCAN_RESULT_NOT_FOUND, "Scan result [" + aId + "] not found.");
		}

		return dtoConverter.scanResultToDto(result);
	}

	@Override
	@Transactional(readOnly = true)
	public ScanStatusDto getScanStatus() {

		ScanService.Status status = scanService.getStatus();

		return status != null ? dtoConverter.scanStatusToDto(status) : null;
	}

}
