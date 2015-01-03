package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.library.ScanEditCommand;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.ScanJobDto;
import net.dorokhov.pony.web.domain.ScanResultDto;
import net.dorokhov.pony.web.domain.ScanStatusDto;
import net.dorokhov.pony.web.domain.command.ScanEditCommandDto;
import net.dorokhov.pony.web.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
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

	@Override
	@Transactional
	public ScanJobDto startScanJob() throws LibraryNotDefinedException {
		return ScanJobDto.valueOf(scanJobService.startScanJob());
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

		return ScanJobDto.valueOf(scanJobService.startEditJob(commandList));
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanJob> page = scanJobService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "updateDate"));

		return ListDto.valueOf(page, new ListDto.ContentConverter<ScanJob, ScanJobDto>() {
			@Override
			public ScanJobDto convert(ScanJob aItem) {
				return ScanJobDto.valueOf(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException {

		ScanJob job = scanJobService.getById(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, "errorScanJobNotFound", "Scan job [" + aId + "] not found.");
		}

		return ScanJobDto.valueOf(job);
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanResult> page = scanService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date"));

		return ListDto.valueOf(page, new ListDto.ContentConverter<ScanResult, ScanResultDto>() {
			@Override
			public ScanResultDto convert(ScanResult aItem) {
				return ScanResultDto.valueOf(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException {

		ScanResult result = scanService.getById(aId);

		if (result == null) {
			throw new ObjectNotFoundException(aId, "errorScanResultNotFound", "Scan result [" + aId + "] not found.");
		}

		return ScanResultDto.valueOf(result);
	}

	@Override
	@Transactional(readOnly = true)
	public ScanStatusDto getScanStatus() {

		ScanService.Status status = scanService.getStatus();

		return status != null ? ScanStatusDto.valueOf(status) : null;
	}

}
