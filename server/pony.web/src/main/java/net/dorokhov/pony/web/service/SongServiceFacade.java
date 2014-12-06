package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ArtistDto;
import net.dorokhov.pony.web.domain.ArtistSongsDto;
import net.dorokhov.pony.web.domain.SearchDto;

import java.util.List;

public interface SongServiceFacade {

	public List<ArtistDto> getArtists();

	public ArtistSongsDto getArtistSongs(String aArtistIdOrName);

	public SearchDto search(String aQuery);

}