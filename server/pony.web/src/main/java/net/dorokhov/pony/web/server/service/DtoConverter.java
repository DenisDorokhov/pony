package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.domain.*;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.web.shared.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DtoConverter {

	public static interface ListConverter<EntityType, DtoType> {
		public DtoType convert(EntityType aItem);
	}

	public AlbumDto albumToDto(Album aAlbum) {

		AlbumDto dto = new AlbumDto();

		dto.setId(aAlbum.getId());
		dto.setName(aAlbum.getName());
		dto.setYear(aAlbum.getYear());

		dto.setArtist(artistToDto(aAlbum.getArtist()));

		StoredFile artwork = aAlbum.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}

	public ArtistDto artistToDto(Artist aArtist) {

		ArtistDto dto = new ArtistDto();

		dto.setId(aArtist.getId());
		dto.setName(aArtist.getName());

		StoredFile artwork = aArtist.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}

	public ArtworkUploadDto artworkToUploadDto(StoredFile aArtwork) {

		ArtworkUploadDto dto = new ArtworkUploadDto();

		dto.setId(aArtwork.getId());
		dto.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + aArtwork.getId()).build().toUriString());

		return dto;
	}

	public GenreDto genreToDto(Genre aGenre) {

		GenreDto dto = new GenreDto();

		dto.setId(aGenre.getId());
		dto.setName(aGenre.getName());

		StoredFile artwork = aGenre.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}

	public InstallationDto installationToDto(Installation aInstallation) {

		InstallationDto dto = new InstallationDto();

		dto.setVersion(aInstallation.getVersion());

		return dto;
	}

	public <EntityType, DtoType> List<DtoType> listToDto(List<EntityType> aList, ListConverter<EntityType, DtoType> aItemConverter) {

		List<DtoType> dto = new ArrayList<>();

		for (EntityType item : aList) {
			dto.add(aItemConverter.convert(item));
		}

		return dto;
	}

	public <EntityType, DtoType> PagedListDto<DtoType> pagedListToDto(Page<EntityType> aPage, ListConverter<EntityType, DtoType> aItemConverter) {

		PagedListDto<DtoType> dto = new PagedListDto<>();

		dto.setPageNumber(aPage.getNumber());
		dto.setPageSize(aPage.getSize());
		dto.setTotalPages(aPage.getTotalPages());
		dto.setTotalElements(aPage.getTotalElements());

		for (EntityType item : aPage.getContent()) {
			dto.getContent().add(aItemConverter.convert(item));
		}

		return dto;
	}

	public LogMessageDto logMessageToDto(LogMessage aLogMessage) {

		LogMessageDto dto = new LogMessageDto();

		dto.setId(aLogMessage.getId());
		dto.setDate(aLogMessage.getDate());
		dto.setCode(aLogMessage.getCode());
		dto.setText(aLogMessage.getText());
		dto.setDetails(aLogMessage.getDetails());

		switch (aLogMessage.getType()) {

			case DEBUG:
				dto.setType(LogMessageDto.Type.DEBUG);
				break;
			case INFO:
				dto.setType(LogMessageDto.Type.INFO);
				break;
			case WARN:
				dto.setType(LogMessageDto.Type.WARN);
				break;
			case ERROR:
				dto.setType(LogMessageDto.Type.ERROR);
				break;
		}

		for (LogMessageArgument argument : aLogMessage.getArguments()) {
			dto.getArguments().add(argument.getValue());
		}

		return dto;
	}

	public ScanJobDto scanJobToDto(ScanJob aScanJob) {

		ScanJobDto dto = new ScanJobDto();

		dto.setId(aScanJob.getId());
		dto.setCreationDate(aScanJob.getCreationDate());
		dto.setUpdateDate(aScanJob.getUpdateDate());
		dto.setScanType(scanTypeToDto(aScanJob.getScanType()));

		switch (aScanJob.getStatus()) {

			case STARTING:
				dto.setStatus(ScanJobDto.Status.STARTING);
				break;

			case STARTED:
				dto.setStatus(ScanJobDto.Status.STARTED);
				break;

			case COMPLETE:
				dto.setStatus(ScanJobDto.Status.COMPLETE);
				break;

			case FAILED:
				dto.setStatus(ScanJobDto.Status.FAILED);
				break;

			case INTERRUPTED:
				dto.setStatus(ScanJobDto.Status.INTERRUPTED);
				break;
		}

		if (aScanJob.getLogMessage() != null) {
			dto.setLogMessage(logMessageToDto(aScanJob.getLogMessage()));
		}
		if (aScanJob.getScanResult() != null) {
			dto.setScanResult(scanResultToDto(aScanJob.getScanResult()));
		}

		return dto;
	}

	public ScanResultDto scanResultToDto(ScanResult aScanResult) {

		ScanResultDto dto = new ScanResultDto();

		dto.setId(aScanResult.getId());

		dto.setDate(aScanResult.getDate());
		dto.setScanType(scanTypeToDto(aScanResult.getScanType()));

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

	public ScanStatusDto scanStatusToDto(ScanService.Status aScanStatus) {

		ScanStatusDto dto = new ScanStatusDto();

		dto.setScanType(scanTypeToDto(aScanStatus.getScanType()));
		dto.setStep(aScanStatus.getStep());
		dto.setTotalSteps(aScanStatus.getTotalSteps());
		dto.setStepCode(aScanStatus.getStepCode());
		dto.setProgress(aScanStatus.getProgress());

		for (File file : aScanStatus.getFiles()) {
			dto.getFiles().add(file.getAbsolutePath());
		}

		return dto;
	}

	public SongDataDto songDataToDto(Song aSong) {

		SongDataDto dto = new SongDataDto();

		dto.setSongId(aSong.getId());

		dto.setDiscNumber(aSong.getDiscNumber());
		dto.setDiscCount(aSong.getDiscCount());

		dto.setTrackNumber(aSong.getTrackNumber());
		dto.setTrackCount(aSong.getTrackCount());

		dto.setTitle(aSong.getName());

		dto.setArtist(aSong.getArtistName());
		dto.setAlbumArtist(aSong.getAlbumArtistName());

		dto.setAlbum(aSong.getAlbumName());

		dto.setYear(aSong.getYear());

		dto.setGenre(aSong.getGenreName());

		StoredFile artwork = aSong.getArtwork();

		if (artwork == null) {
			artwork = aSong.getAlbum().getArtwork();
		}

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		dto.setPath(aSong.getPath());
		dto.setFormat(aSong.getFormat());
		dto.setMimeType(aSong.getMimeType());
		dto.setSize(aSong.getSize());
		dto.setDuration(aSong.getDuration());
		dto.setBitRate(aSong.getBitRate());

		return dto;
	}

	public SongDto songToDto(Song aSong) {

		SongDto dto = new SongDto();

		dto.setId(aSong.getId());
		dto.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/audio/" + aSong.getId()).build().toUriString());
		dto.setDuration(aSong.getDuration());
		dto.setDiscNumber(aSong.getDiscNumber());
		dto.setTrackNumber(aSong.getTrackNumber());
		dto.setName(aSong.getName() != null ? aSong.getName() : new File(aSong.getPath()).getName());

		dto.setArtistName(aSong.getAlbumArtistName());
		if (aSong.getArtistName() != null) {
			dto.setArtistName(aSong.getArtistName());
		}

		dto.setGenre(genreToDto(aSong.getGenre()));
		dto.setAlbum(albumToDto(aSong.getAlbum()));

		return dto;
	}

	public UserDto userToDto(User aUser) {

		UserDto dto = new UserDto();

		dto.setId(aUser.getId());
		dto.setName(aUser.getName());
		dto.setEmail(aUser.getEmail());

		if (aUser.getRoles().contains(RoleDto.ADMIN.toString())) {
			dto.setRole(RoleDto.ADMIN);
		} else if (aUser.getRoles().contains(RoleDto.USER.toString())) {
			dto.setRole(RoleDto.USER);
		}

		return dto;
	}

	private ScanTypeDto scanTypeToDto(ScanType aScanType) {

		ScanTypeDto dto = null;

		switch (aScanType) {

			case EDIT:
				dto = ScanTypeDto.EDIT;
				break;

			case FULL:
				dto = ScanTypeDto.FULL;
				break;
		}

		return dto;
	}

}
