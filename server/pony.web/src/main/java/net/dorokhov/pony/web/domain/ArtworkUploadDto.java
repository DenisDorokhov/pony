package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ArtworkUploadDto {

	private Long id;

	private String url;

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

	public static ArtworkUploadDto valueOf(StoredFile aArtwork) {

		ArtworkUploadDto dto = new ArtworkUploadDto();

		dto.setId(aArtwork.getId());
		dto.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + aArtwork.getId()).build().toUriString());

		return dto;
	}
}
