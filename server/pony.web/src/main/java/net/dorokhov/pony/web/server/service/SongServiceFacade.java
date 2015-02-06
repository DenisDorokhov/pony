package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.*;

import java.util.List;

public interface SongServiceFacade {

	public List<ArtistDto> getArtists();

	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName) throws ObjectNotFoundException;

	public SearchDto search(SearchQueryDto aQuery);

	public List<SongDto> getRandomSongs(int aCount) throws InvalidArgumentException;

	public List<SongDto> getRandomArtistSongs(int aCount, String aArtistIdOrName) throws InvalidArgumentException;

	public List<SongDataDto> getSongData(List<Long> aSongIds) throws ObjectNotFoundException, InvalidArgumentException;

}
