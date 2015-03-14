package net.dorokhov.pony.web.client.control.status;

import com.google.gwt.resources.client.ImageResource;

public interface StatusIndicator {

	public ImageResource getIcon();
	public void setIcon(ImageResource aIcon);

	public String getText();
	public void setText(String aText);

}
