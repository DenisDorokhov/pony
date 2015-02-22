package net.dorokhov.pony.web.shared;

public class ArtworkUploadDto extends AbstractDto<Long> {

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		url = aUrl;
	}

}
