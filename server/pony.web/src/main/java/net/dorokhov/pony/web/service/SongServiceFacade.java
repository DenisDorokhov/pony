package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ArtistDto;
import net.dorokhov.pony.web.domain.ArtistAlbumsDto;
import net.dorokhov.pony.web.domain.SearchDto;
import net.dorokhov.pony.web.domain.SearchQueryDto;

import java.util.List;

public interface SongServiceFacade {

	public List<ArtistDto> getArtists();

	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName);

	public SearchDto search(SearchQueryDto aQuery);

}
