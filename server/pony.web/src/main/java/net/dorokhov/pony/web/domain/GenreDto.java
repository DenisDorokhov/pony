package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.Genre;
import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

	public static GenreDto valueOf(Genre aGenre) {

		GenreDto dto = new GenreDto();

		dto.setId(aGenre.getId());
		dto.setName(aGenre.getName());

		StoredFile artwork = aGenre.getArtwork();

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		return dto;
	}

}
