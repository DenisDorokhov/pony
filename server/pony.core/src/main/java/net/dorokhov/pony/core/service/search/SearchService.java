package net.dorokhov.pony.core.service.search;

import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;

import java.util.List;

public interface SearchService {

	public void createIndex();

	public void clearIndex();

	public List<Artist> searchArtists(String aQuery, int aMaxResults);
	public List<Album> searchAlbums(String aQuery, int aMaxResults);
	public List<Song> searchSongs(String aQuery, int aMaxResults);

}
