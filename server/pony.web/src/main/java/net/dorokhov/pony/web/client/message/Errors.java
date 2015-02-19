package net.dorokhov.pony.web.client.message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Errors extends ConstantsWithLookup {

	public static final Errors INSTANCE = GWT.create(Errors.class);

}
