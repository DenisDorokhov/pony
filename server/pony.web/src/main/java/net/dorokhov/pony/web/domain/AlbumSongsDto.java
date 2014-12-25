package net.dorokhov.pony.web.domain;

import java.util.ArrayList;
import java.util.List;

public class AlbumSongsDto {

	private AlbumDto album;

	private List<SongDto> songs;

	public AlbumDto getAlbum() {
		return album;
	}

	public void setAlbum(AlbumDto aAlbum) {
		album = aAlbum;
	}

	public List<SongDto> getSongs() {

		if (songs == null) {
			songs = new ArrayList<>();
		}

		return songs;
	}

	public void setSongs(List<SongDto> aSongs) {
		songs = aSongs;
	}

}
