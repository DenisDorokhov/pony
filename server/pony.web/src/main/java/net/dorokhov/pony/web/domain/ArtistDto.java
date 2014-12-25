package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ArtistDto {

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

	public static ArtistDto valueOf(Artist aArtist) {

		ArtistDto dto = new ArtistDto();

		dto.setId(aArtist.getId());
		dto.setName(aArtist.getName());

		StoredFile artwork = aArtist.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}

}
