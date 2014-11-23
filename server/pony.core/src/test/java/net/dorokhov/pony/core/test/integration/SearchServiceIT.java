package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.GenreDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Genre;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SearchServiceIT extends AbstractIntegrationCase {

	private GenreDao genreDao;
	private ArtistDao artistDao;
	private AlbumDao albumDao;
	private SongDao songDao;

	private SearchService searchService;

	@Before
	public void setUp() throws Exception {
		genreDao = context.getBean(GenreDao.class);
		artistDao = context.getBean(ArtistDao.class);
		albumDao = context.getBean(AlbumDao.class);
		songDao = context.getBean(SongDao.class);
		searchService = context.getBean(SearchService.class);
	}

	@Test
	public void test() {

		searchService.createIndex();

		Assert.assertEquals(0, searchService.searchArtists("art foo", 10).size());
		Assert.assertEquals(0, searchService.searchAlbums("alb foo", 10).size());
		Assert.assertEquals(0, searchService.searchSongs("so foo", 10).size());

		Genre genre = buildGenre();

		genre = genreDao.save(genre);

		Artist artist = buildArtist();

		artist = artistDao.save(artist);

		Album album = buildAlbum(artist);

		album = albumDao.save(album);

		songDao.save(buildSong(album, genre));

		Assert.assertEquals(1, searchService.searchGenres("gen foo", 10).size());
		Assert.assertEquals(1, searchService.searchArtists("the art Foo", 10).size());
		Assert.assertEquals(1, searchService.searchAlbums("Alb of foo", 10).size());
		Assert.assertEquals(1, searchService.searchSongs("the So foo", 10).size());

		Assert.assertEquals(0, searchService.searchArtists("genre2", 10).size());
		Assert.assertEquals(0, searchService.searchArtists("artist2", 10).size());
		Assert.assertEquals(0, searchService.searchAlbums("album2", 10).size());
		Assert.assertEquals(0, searchService.searchSongs("song2", 10).size());

		searchService.clearIndex();

		Assert.assertEquals(0, searchService.searchGenres("gen foo", 10).size());
		Assert.assertEquals(0, searchService.searchArtists("art foo", 10).size());
		Assert.assertEquals(0, searchService.searchAlbums("alb foo", 10).size());
		Assert.assertEquals(0, searchService.searchSongs("so foo", 10).size());
	}

	private Artist buildArtist() {

		Artist artist = new Artist();

		artist.setName("The artist1 foobar");

		return artist;
	}

	private Album buildAlbum(Artist aArtist) {

		Album album = new Album();

		album.setName("album1 Of FOOBAR");
		album.setArtist(aArtist);

		return album;
	}

	private Genre buildGenre() {

		Genre genre = new Genre();

		genre.setName("genre1 foobar");

		return genre;
	}

	private Song buildSong(Album aAlbum, Genre aGenre) {

		Song song = new Song();

		song.setPath("path");
		song.setFormat("type");
		song.setMimeType("audio/mpeg");
		song.setSize(1000L);

		song.setDuration(100);
		song.setBitRate(2000L);

		song.setName("song1 foobar");

		song.setAlbum(aAlbum);
		song.setGenre(aGenre);

		return song;
	}

}
