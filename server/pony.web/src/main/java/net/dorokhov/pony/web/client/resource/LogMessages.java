package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface LogMessages extends ConstantsWithLookup {

	public static final LogMessages INSTANCE = GWT.create(LogMessages.class);

}
