package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface Styles extends ClientBundle {

	public static final Styles INSTANCE = GWT.create(Styles.class);

	@Source("CommonStyle.css")
	public CommonStyle commonStyle();

}
