package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Genre;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.web.domain.SearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchServiceFacadeImpl implements SearchServiceFacade {

	private static final int MAX_SEARCH_RESULTS = 10;

	private SearchService searchService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setSearchService(SearchService aSearchService) {
		searchService = aSearchService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional(readOnly = true)
	public SearchDto search(String aQuery) {

		List<Genre> genreList = searchService.searchGenres(aQuery, MAX_SEARCH_RESULTS);
		List<Artist> artistList = searchService.searchArtists(aQuery, MAX_SEARCH_RESULTS);
		List<Album> albumList = searchService.searchAlbums(aQuery, MAX_SEARCH_RESULTS);
		List<Song> songList = searchService.searchSongs(aQuery, MAX_SEARCH_RESULTS);

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
}
