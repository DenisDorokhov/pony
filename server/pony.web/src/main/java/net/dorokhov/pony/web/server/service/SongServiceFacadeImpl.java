package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.common.RandomEntityFetcher;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Genre;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SongServiceFacadeImpl implements SongServiceFacade {

	private static final int SEARCH_RESULTS_COUNT = 10;

	private static final int MAX_RANDOM_SONGS = 30;
	private static final int MAX_SONG_DATA = 500;

	private ArtistDao artistDao;

	private SongDao songDao;

	private SearchService searchService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setArtistDao(ArtistDao aArtistDao) {
		artistDao = aArtistDao;
	}

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Autowired
	public void setSearchService(SearchService aSearchService) {
		searchService = aSearchService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	public List<ArtistDto> getArtists() {

		List<Artist> artistList = artistDao.findAll(new Sort("name"));

		Collections.sort(artistList);

		List<ArtistDto> dto = new ArrayList<>();

		for (Artist artist : artistList) {
			dto.add(dtoConverter.artistToDto(artist));
		}

		return dto;
	}

	@Override
	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName) throws ObjectNotFoundException {

		Artist artist = null;

		if (StringUtils.isNumeric(aArtistIdOrName)) {
			artist = artistDao.findOne(NumberUtils.toLong(aArtistIdOrName));
		}

		if (artist == null) {
			artist = artistDao.findByName(aArtistIdOrName);
		}

		if (artist != null) {

			List<Song> songList = songDao.findByAlbumArtistId(artist.getId(),
					new Sort("album.year", "album.name", "discNumber", "trackNumber", "name"));

			Collections.sort(songList);

			ArtistAlbumsDto dto = new ArtistAlbumsDto();

			dto.setArtist(dtoConverter.artistToDto(artist));
			dto.setAlbums(songListToDto(songList));

			return dto;
		}

		throw new ObjectNotFoundException(aArtistIdOrName, "errorArtistNotFound", "Artist [" + aArtistIdOrName + "] not found.");
	}

	@Override
	@Transactional(readOnly = true)
	public SearchDto search(SearchQueryDto aQuery) {

		List<Genre> genreList = searchService.searchGenres(aQuery.getText(), SEARCH_RESULTS_COUNT);
		List<Artist> artistList = searchService.searchArtists(aQuery.getText(), SEARCH_RESULTS_COUNT);
		List<Album> albumList = searchService.searchAlbums(aQuery.getText(), SEARCH_RESULTS_COUNT);
		List<Song> songList = searchService.searchSongs(aQuery.getText(), SEARCH_RESULTS_COUNT);

		SearchDto dto = new SearchDto();

		dto.setQuery(aQuery);

		for (Genre genre : genreList) {
			dto.getGenres().add(dtoConverter.genreToDto(genre));
		}
		for (Artist artist : artistList) {
			dto.getArtists().add(dtoConverter.artistToDto(artist));
		}
		for (Album album : albumList) {
			dto.getAlbums().add(dtoConverter.albumToDto(album));
		}
		for (Song song : songList) {
			dto.getSongs().add(dtoConverter.songToDto(song));
		}

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SongDto> getRandomSongs(int aCount) throws InvalidArgumentException {

		if (aCount > MAX_RANDOM_SONGS) {
			throw new InvalidArgumentException("errorRandomSongsCountInvalid", "Number of random songs [" + aCount + "] must be less than or equal to [" + 100 + "]",
					String.valueOf(aCount), String.valueOf(MAX_RANDOM_SONGS));
		}

		List<Song> songList = new RandomEntityFetcher<Song>().fetch(aCount, new RandomEntityFetcher.Dao<Song>() {

			@Override
			public long fetchCount() {
				return songDao.count();
			}

			@Override
			public Page<Song> fetchContent(Pageable aPageable) {
				return songDao.findAll(aPageable);
			}
		});

		List<SongDto> dto = new ArrayList<>();

		for (Song song : songList) {
			dto.add(dtoConverter.songToDto(song));
		}

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SongDto> getRandomArtistSongs(int aCount, String aArtistIdOrName) throws InvalidArgumentException {

		if (aCount > MAX_RANDOM_SONGS) {
			throw new InvalidArgumentException("errorRandomSongsCountInvalid", "Number of random songs [" + aCount + "] must be less than or equal to [" + 100 + "]",
					String.valueOf(aCount), String.valueOf(MAX_RANDOM_SONGS));
		}

		Artist artist = null;

		if (StringUtils.isNumeric(aArtistIdOrName)) {
			artist = artistDao.findOne(NumberUtils.toLong(aArtistIdOrName));
		}

		if (artist == null) {
			artist = artistDao.findByName(aArtistIdOrName);
		}

		List<SongDto> dto = new ArrayList<>();

		if (artist != null) {

			final Long artistId = artist.getId();

			List<Song> songList = new RandomEntityFetcher<Song>().fetch(aCount, new RandomEntityFetcher.Dao<Song>() {

				@Override
				public long fetchCount() {
					return songDao.countByAlbumArtistId(artistId);
				}

				@Override
				public Page<Song> fetchContent(Pageable aPageable) {
					return songDao.findByAlbumArtistId(artistId, aPageable);
				}
			});

			for (Song song : songList) {
				dto.add(dtoConverter.songToDto(song));
			}
		}

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SongDataDto> getSongData(List<Long> aSongIds) throws ObjectNotFoundException, InvalidArgumentException {

		if (aSongIds.size() > MAX_SONG_DATA) {
			throw new InvalidArgumentException("errorSongsCountInvalid", "Songs count [" + aSongIds.size() + "] must be less than or equal to [" + MAX_SONG_DATA + "]",
					String.valueOf(aSongIds.size()), String.valueOf(MAX_SONG_DATA));
		}

		Set<Long> idSet = new HashSet<>(aSongIds);

		List<SongDataDto> dto = new ArrayList<>();

		for (Song song : songDao.findAll(aSongIds)) {

			dto.add(dtoConverter.songDataToDto(song));

			idSet.remove(song.getId());
		}

		if (idSet.size() > 0) {

			Long songId = idSet.iterator().next();

			throw new ObjectNotFoundException(songId, "errorSongNotFound", "Song [" + songId + "] not found.");
		}

		return dto;
	}

	private ArrayList<AlbumSongsDto> songListToDto(List<Song> aSongList) {

		ArrayList<AlbumSongsDto> result = new ArrayList<>();

		AlbumSongsDto currentDto = null;

		for (Song song : aSongList) {

			if (currentDto == null || !currentDto.getAlbum().getId().equals(song.getAlbum().getId())) {

				currentDto = new AlbumSongsDto();
				currentDto.setAlbum(dtoConverter.albumToDto(song.getAlbum()));

				result.add(currentDto);
			}

			currentDto.getSongs().add(dtoConverter.songToDto(song));
		}

		return result;
	}

}
