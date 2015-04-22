package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Scripts extends ClientBundle {

	public static final Scripts INSTANCE = GWT.create(Scripts.class);

	@Source("bootstrap-growl.js")
	TextResource growl();

	@Source("UnityShim.js")
	TextResource unity();

	@Source("ua-parser.js")
	TextResource uaParser();

}
