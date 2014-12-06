package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.*;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.web.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.util.List;

@Service
public class DtoConverter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public static interface ItemConverter<FromType, ToType extends Serializable> {
		public ToType convert(FromType aItem);
	}

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

	public GenreDto genreToDto(Genre aGenre) {

		GenreDto dto = new GenreDto();

		dto.setId(aGenre.getId());
		dto.setName(aGenre.getName());

		StoredFile artwork = aGenre.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(buildFileUrl(artwork.getId()));
		}

		return dto;
	}

	public ArtistDto artistToDto(Artist aArtist) {

		ArtistDto dto = new ArtistDto();

		initArtistDto(dto, aArtist);

		return dto;
	}

	public ArtistSongsDto artistToSongsDto(Artist aArtist, List<AlbumSongsDto> aAlbumsDto) {

		ArtistSongsDto dto = new ArtistSongsDto();

		initArtistDto(dto, aArtist);

		dto.setAlbums(aAlbumsDto);

		return dto;
	}

	public AlbumDto albumToDto(Album aAlbum) {

		AlbumDto dto = new AlbumDto();

		initAlbumDto(dto, aAlbum);

		return dto;
	}

	public AlbumSongsDto albumToSongsDto(Album aAlbum, List<SongDto> aSongsDto) {

		AlbumSongsDto dto = new AlbumSongsDto();

		initAlbumDto(dto, aAlbum);

		dto.setSongs(aSongsDto);

		return dto;
	}

	public SongDto songToDto(Song aSong) {

		SongDto dto = new SongDto();

		dto.setId(aSong.getId());
		dto.setUrl(buildAudioUrl(aSong.getId()));
		dto.setDuration(aSong.getDuration());
		dto.setDiscNumber(aSong.getDiscNumber());
		dto.setTrackNumber(aSong.getTrackNumber());
		dto.setName(aSong.getName());

		dto.setGenre(aSong.getGenre().getId());
		dto.setGenreName(aSong.getGenre().getName());

		dto.setArtist(aSong.getAlbum().getArtist().getId());
		dto.setArtistName(aSong.getAlbum().getArtist().getName());

		dto.setAlbum(aSong.getAlbum().getId());
		dto.setAlbumName(aSong.getAlbum().getName());
		dto.setAlbumArtistName(aSong.getAlbumArtistName());
		dto.setAlbumYear(aSong.getAlbum().getYear());

		StoredFile artwork = aSong.getArtwork();

		if (artwork == null) {
			artwork = aSong.getAlbum().getArtwork();
		}

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(buildFileUrl(artwork.getId()));
		}

		return dto;
	}

	public UserDto userToDto(User aUser) {

		UserDto dto = new UserDto();

		dto.setId(aUser.getId());
		dto.setName(aUser.getName());
		dto.setEmail(aUser.getEmail());

		// TODO: set role
		//if (aUser.hasRole("admin")) {
		//	dto.setRole("admin");
		//} else if (aUser.hasRole("user")) {
		//	dto.setRole("user");
		//}

		return dto;
	}

	public UserTokenDto userTokenToDto(UserToken aToken) {

		UserTokenDto dto = new UserTokenDto();

		dto.setId(aToken.getId());

		return dto;
	}

	private void initArtistDto(ArtistDto aDto, Artist aArtist) {

		aDto.setId(aArtist.getId());
		aDto.setName(aArtist.getName());

		StoredFile artwork = aArtist.getArtwork();

		if (artwork != null) {
			aDto.setArtwork(artwork.getId());
			aDto.setArtworkUrl(buildFileUrl(artwork.getId()));
		}
	}

	private void initAlbumDto(AlbumDto aDto, Album aAlbum) {

		aDto.setId(aAlbum.getId());
		aDto.setName(aAlbum.getName());
		aDto.setYear(aAlbum.getYear());

		aDto.setArtist(aAlbum.getArtist().getId());
		aDto.setArtistName(aAlbum.getArtist().getName());

		StoredFile artwork = aAlbum.getArtwork();

		if (artwork != null) {
			aDto.setArtwork(artwork.getId());
			aDto.setArtworkUrl(buildFileUrl(artwork.getId()));
		}
	}

	private String buildFileUrl(Long aId) {
		return buildUrl(aId, "files");
	}

	private String buildAudioUrl(Long aId) {
		return buildUrl(aId, "audio");
	}

	private String buildUrl(Long aId, String aCall) {

		String url = null;

		if (aId != null) {

			HttpServletRequest request = getCurrentRequest();

			if (request != null) {
				url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
						request.getContextPath() + "/" + aCall + "/" + aId;
			}
		}

		return url;
	}

	private HttpServletRequest getCurrentRequest() {

		HttpServletRequest request = null;

		try {

			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

			request = attributes.getRequest();

		} catch (Exception e) {
			log.warn("could not get current request, is it a web application?");
		}

		return request;
	}

}
