package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.*;
import net.dorokhov.pony.web.shared.list.ArtistListDto;
import net.dorokhov.pony.web.shared.list.SongDataListDto;
import net.dorokhov.pony.web.shared.list.SongListDto;

import java.util.List;

public interface SongServiceFacade {

	public ArtistListDto getArtists();

	public ArtistAlbumsDto getArtistSongs(String aArtistIdOrName) throws ObjectNotFoundException;

	public SearchDto search(SearchQueryDto aQuery);

	public SongListDto getRandomSongs(int aCount) throws InvalidArgumentException;

	public SongListDto getRandomArtistSongs(int aCount, String aArtistIdOrName) throws InvalidArgumentException;

	public SongDataListDto getSongData(List<Long> aSongIds) throws ObjectNotFoundException, InvalidArgumentException;

}
