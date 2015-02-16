package net.dorokhov.pony.web.shared;

import java.util.ArrayList;
import java.util.List;

public class ListDto<T> {

	private List<T> content;

	public List<T> getContent() {

		if (content == null) {
			content = new ArrayList<>();
		}

		return content;
	}

	public void setContent(List<T> aContent) {
		content = aContent;
	}

}
