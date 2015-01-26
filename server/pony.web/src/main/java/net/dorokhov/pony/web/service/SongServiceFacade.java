package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.*;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;

import java.util.List;

public interface SongServiceFacade {

	public List<ArtistDto> getArtists();

	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName) throws ObjectNotFoundException;

	public SearchDto search(SearchQueryDto aQuery);

	public List<SongDto> getRandomSongs(int aCount);

	public List<SongDto> getRandomArtistSongs(int aCount, String aArtistIdOrName);

	public List<SongDataDto> getSongData(List<Long> aSongIds) throws ObjectNotFoundException;

}
