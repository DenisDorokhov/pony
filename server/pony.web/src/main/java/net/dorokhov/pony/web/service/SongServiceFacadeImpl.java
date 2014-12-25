package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Genre;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.web.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SongServiceFacadeImpl implements SongServiceFacade {

	private static final int MAX_SEARCH_RESULTS = 10;

	private ArtistDao artistDao;

	private SongDao songDao;

	private SearchService searchService;

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

	@Override
	public List<ArtistDto> getArtists() {

		List<Artist> artistList = artistDao.findAll(new Sort("name"));

		Collections.sort(artistList);

		List<ArtistDto> dto = new ArrayList<>();

		for (Artist artist : artistList) {
			dto.add(ArtistDto.valueOf(artist));
		}

		return dto;
	}

	@Override
	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName) {

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

			dto.setArtist(ArtistDto.valueOf(artist));
			dto.setAlbums(songListToDto(songList));

			return dto;
		}

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public SearchDto search(SearchQueryDto aQuery) {

		List<Genre> genreList = searchService.searchGenres(aQuery.getText(), MAX_SEARCH_RESULTS);
		List<Artist> artistList = searchService.searchArtists(aQuery.getText(), MAX_SEARCH_RESULTS);
		List<Album> albumList = searchService.searchAlbums(aQuery.getText(), MAX_SEARCH_RESULTS);
		List<Song> songList = searchService.searchSongs(aQuery.getText(), MAX_SEARCH_RESULTS);

		SearchDto dto = new SearchDto();

		dto.setQuery(aQuery);

		for (Genre genre : genreList) {
			dto.getGenres().add(GenreDto.valueOf(genre));
		}
		for (Artist artist : artistList) {
			dto.getArtists().add(ArtistDto.valueOf(artist));
		}
		for (Album album : albumList) {
			dto.getAlbums().add(AlbumDto.valueOf(album));
		}
		for (Song song : songList) {
			dto.getSongs().add(SongDto.valueOf(song));
		}

		return dto;
	}

	private ArrayList<AlbumSongsDto> songListToDto(List<Song> aSongList) {

		ArrayList<AlbumSongsDto> result = new ArrayList<>();

		AlbumSongsDto currentDto = null;

		for (Song song : aSongList) {

			if (currentDto == null || !currentDto.getAlbum().getId().equals(song.getAlbum().getId())) {

				currentDto = new AlbumSongsDto();
				currentDto.setAlbum(AlbumDto.valueOf(song.getAlbum()));

				result.add(currentDto);
			}

			currentDto.getSongs().add(SongDto.valueOf(song));
		}

		return result;
	}
}
