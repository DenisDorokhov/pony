package net.dorokhov.pony.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchDto implements Serializable {

	private String query;

	private List<GenreDto> genres;

	private List<ArtistDto> artists;

	private List<AlbumDto> albums;

	private List<SongDto> songs;

	public String getQuery() {
		return query;
	}

	public void setQuery(String aQuery) {
		query = aQuery;
	}

	public List<GenreDto> getGenres() {

		if (genres == null) {
			genres = new ArrayList<>();
		}

		return genres;
	}

	public void setGenres(List<GenreDto> aGenres) {
		genres = aGenres;
	}

	public List<ArtistDto> getArtists() {

		if (artists == null) {
			artists = new ArrayList<>();
		}

		return artists;
	}

	public void setArtists(List<ArtistDto> aArtists) {
		artists = aArtists;
	}

	public List<AlbumDto> getAlbums() {

		if (albums == null) {
			albums = new ArrayList<>();
		}

		return albums;
	}

	public void setAlbums(List<AlbumDto> aAlbums) {
		albums = aAlbums;
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
