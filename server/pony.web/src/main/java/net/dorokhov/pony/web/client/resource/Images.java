package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE = GWT.create(Images.class);

	@Source("spinner.gif")
	ImageResource spinner();

	@Source("error.png")
	ImageResource error();

	@Source("unknown.png")
	ImageResource unknown();

}
