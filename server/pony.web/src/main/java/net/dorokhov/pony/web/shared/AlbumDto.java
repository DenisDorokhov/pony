package net.dorokhov.pony.web.shared;

public class AlbumDto extends AbstractDto<Long> {

	private String name;

	private Integer year;

	private Long artwork;

	private String artworkUrl;

	private String url;

	private ArtistDto artist;

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer aYear) {
		year = aYear;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		url = aUrl;
	}

	public ArtistDto getArtist() {
		return artist;
	}

	public void setArtist(ArtistDto aArtist) {
		artist = aArtist;
	}
}
