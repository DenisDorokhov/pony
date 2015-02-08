package net.dorokhov.pony.web.shared;

public class GenreDto {

	private Long id;

	private String name;

	private Long artwork;

	private String artworkUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
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

}
