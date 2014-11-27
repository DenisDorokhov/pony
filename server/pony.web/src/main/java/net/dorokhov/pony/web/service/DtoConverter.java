package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.*;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.web.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Serializable;

@Service
public class DtoConverter {

	public <EntityType, DtoType extends Serializable> ListDto<DtoType> pageToListDto(Page<EntityType> aPage, ItemConverter<EntityType, DtoType> aItemConverter) {

		ListDto<DtoType> dto = new ListDto<>();

		dto.setPageNumber(aPage.getNumber());
		dto.setPageSize(aPage.getSize());
		dto.setTotalPages(aPage.getTotalPages());
		dto.setTotalElements(aPage.getTotalElements());

		for (EntityType item : aPage.getContent()) {
			dto.getContent().add(aItemConverter.convert(item));
		}

		return dto;
	}

	public InstallationDto installationToDto(Installation aInstallation) {

		InstallationDto dto = new InstallationDto();

		dto.setVersion(aInstallation.getVersion());

		return dto;
	}

	public LogMessageDto logMessageToDto(LogMessage aLogMessage) {

		LogMessageDto dto = new LogMessageDto();

		dto.setId(aLogMessage.getId());
		dto.setDate(aLogMessage.getDate());
		dto.setType(aLogMessage.getType());
		dto.setCode(aLogMessage.getCode());
		dto.setText(aLogMessage.getText());
		dto.setDetails(aLogMessage.getDetails());

		for (LogMessageArgument argument : aLogMessage.getArguments()) {
			dto.getArguments().add(argument.getValue());
		}

		return dto;
	}

	public ScanResultDto scanResultToDto(ScanResult aScanResult) {

		ScanResultDto dto = new ScanResultDto();

		dto.setId(aScanResult.getId());

		dto.setDate(aScanResult.getDate());
		dto.setScanType(aScanResult.getScanType());

		dto.setTargetPaths(aScanResult.getTargetPaths());
		dto.setFailedPaths(aScanResult.getFailedPaths());

		dto.setDuration(aScanResult.getDuration());

		dto.setSongSize(aScanResult.getSongSize());
		dto.setArtworkSize(aScanResult.getArtworkSize());

		dto.setGenreCount(aScanResult.getGenreCount());
		dto.setArtistCount(aScanResult.getArtistCount());
		dto.setAlbumCount(aScanResult.getAlbumCount());
		dto.setSongCount(aScanResult.getSongCount());
		dto.setArtworkCount(aScanResult.getArtworkCount());

		dto.setProcessedSongCount(aScanResult.getProcessedSongCount());

		dto.setCreatedArtistCount(aScanResult.getCreatedArtistCount());
		dto.setUpdatedArtistCount(aScanResult.getUpdatedArtistCount());
		dto.setDeletedArtistCount(aScanResult.getDeletedArtistCount());

		dto.setCreatedAlbumCount(aScanResult.getCreatedAlbumCount());
		dto.setUpdatedAlbumCount(aScanResult.getUpdatedAlbumCount());
		dto.setDeletedAlbumCount(aScanResult.getDeletedAlbumCount());

		dto.setCreatedGenreCount(aScanResult.getCreatedGenreCount());
		dto.setUpdatedGenreCount(aScanResult.getUpdatedGenreCount());
		dto.setDeletedGenreCount(aScanResult.getDeletedGenreCount());

		dto.setCreatedSongCount(aScanResult.getCreatedSongCount());
		dto.setUpdatedSongCount(aScanResult.getUpdatedSongCount());
		dto.setDeletedSongCount(aScanResult.getDeletedSongCount());

		dto.setCreatedArtworkCount(aScanResult.getCreatedArtworkCount());
		dto.setDeletedArtworkCount(aScanResult.getDeletedArtworkCount());

		return dto;
	}

	public ScanJobDto scanJobToDto(ScanJob aScanJob) {

		ScanJobDto dto = new ScanJobDto();

		dto.setId(aScanJob.getId());
		dto.setCreationDate(aScanJob.getCreationDate());
		dto.setUpdateDate(aScanJob.getUpdateDate());
		dto.setScanType(aScanJob.getScanType());
		dto.setStatus(aScanJob.getStatus());

		if (aScanJob.getLogMessage() != null) {
			dto.setLogMessage(logMessageToDto(aScanJob.getLogMessage()));
		}
		if (aScanJob.getScanResult() != null) {
			dto.setScanResult(scanResultToDto(aScanJob.getScanResult()));
		}

		return dto;
	}

	public ScanStatusDto scanStatusToDto(ScanService.Status aScanStatus) {

		ScanStatusDto dto = new ScanStatusDto();

		dto.setScanType(aScanStatus.getScanType());
		dto.setStep(aScanStatus.getStep());
		dto.setTotalSteps(aScanStatus.getTotalSteps());
		dto.setStepCode(aScanStatus.getStepCode());
		dto.setProgress(aScanStatus.getProgress());

		for (File file : aScanStatus.getFiles()) {
			dto.getFiles().add(file.getAbsolutePath());
		}

		return dto;
	}

	public static interface ItemConverter<FromType, ToType extends Serializable> {
		public ToType convert(FromType aItem);
	}

}
