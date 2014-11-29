package net.dorokhov.pony.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArtistSongsDto extends ArtistDto implements Serializable {

	private List<AlbumSongsDto> albums;

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
