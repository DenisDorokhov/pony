package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class SongDto {

	private Long id;

	private String url;

	private Integer duration;

	private Integer discNumber;

	private Integer trackNumber;

	private String name;

	private Long artwork;

	private String artworkUrl;

	private Long genre;

	private String genreName;

	private Long artist;

	private String artistName;

	private Long album;

	private String albumName;

	private String albumArtistName;

	private Integer albumYear;

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		url = aUrl;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer aDuration) {
		duration = aDuration;
	}

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer aDiscNumber) {
		discNumber = aDiscNumber;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer aTrackNumber) {
		trackNumber = aTrackNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	public Long getArtwork() {
		return artwork;
	}

	public void setArtwork(Long aArtwork) {
		artwork = aArtwork;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	public void setArtworkUrl(String aArtworkUrl) {
		artworkUrl = aArtworkUrl;
	}

	public Long getGenre() {
		return genre;
	}

	public void setGenre(Long aGenre) {
		genre = aGenre;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String aGenreName) {
		genreName = aGenreName;
	}

	public Long getArtist() {
		return artist;
	}

	public void setArtist(Long aArtist) {
		artist = aArtist;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String aArtistName) {
		artistName = aArtistName;
	}

	public Long getAlbum() {
		return album;
	}

	public void setAlbum(Long aAlbum) {
		album = aAlbum;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String aAlbumName) {
		albumName = aAlbumName;
	}

	public String getAlbumArtistName() {
		return albumArtistName;
	}

	public void setAlbumArtistName(String aAlbumArtistName) {
		albumArtistName = aAlbumArtistName;
	}

	public Integer getAlbumYear() {
		return albumYear;
	}

	public void setAlbumYear(Integer aAlbumYear) {
		albumYear = aAlbumYear;
	}

	public static SongDto valueOf(Song aSong) {

		SongDto dto = new SongDto();

		dto.setId(aSong.getId());
		dto.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/audio/" + aSong.getId()).build().toUriString());
		dto.setDuration(aSong.getDuration());
		dto.setDiscNumber(aSong.getDiscNumber());
		dto.setTrackNumber(aSong.getTrackNumber());
		dto.setName(aSong.getName());

		dto.setGenre(aSong.getGenre().getId());
		dto.setGenreName(aSong.getGenre().getName());

		dto.setArtist(aSong.getAlbum().getArtist().getId());
		dto.setArtistName(aSong.getAlbum().getArtist().getName());

		dto.setAlbum(aSong.getAlbum().getId());
		dto.setAlbumName(aSong.getAlbum().getName());
		dto.setAlbumArtistName(aSong.getAlbumArtistName());
		dto.setAlbumYear(aSong.getAlbum().getYear());

		StoredFile artwork = aSong.getArtwork();

		if (artwork == null) {
			artwork = aSong.getAlbum().getArtwork();
		}

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}
}
