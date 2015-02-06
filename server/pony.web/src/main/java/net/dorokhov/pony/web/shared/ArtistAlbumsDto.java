package net.dorokhov.pony.web.shared;

import java.util.ArrayList;
import java.util.List;

public class ArtistAlbumsDto {

	private ArtistDto artist;

	private List<AlbumSongsDto> albums;

	public ArtistDto getArtist() {
		return artist;
	}

	public void setArtist(ArtistDto aArtist) {
		artist = aArtist;
	}

	public List<AlbumSongsDto> getAlbums() {

		if (albums == null) {
			albums = new ArrayList<>();
		}

		return albums;
	}

	public void setAlbums(List<AlbumSongsDto> aAlbums) {
		albums = aAlbums;
	}

}
