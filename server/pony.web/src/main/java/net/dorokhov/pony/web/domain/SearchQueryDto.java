package net.dorokhov.pony.web.domain;

import org.hibernate.validator.constraints.NotBlank;

public class SearchQueryDto {

	private String text;

	@NotBlank
	public String getText() {
		return text;
	}

	public void setText(String aText) {
		text = aText;
	}

}
