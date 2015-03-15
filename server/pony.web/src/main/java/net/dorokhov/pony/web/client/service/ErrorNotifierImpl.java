package net.dorokhov.pony.web.client.service;

import com.google.gwt.core.client.ScriptInjector;
import net.dorokhov.pony.web.client.util.ErrorUtils;
import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.List;

public class ErrorNotifierImpl implements ErrorNotifier {

	public ErrorNotifierImpl() {
		ScriptInjector.fromUrl("js/bootstrap-growl.js").setWindow(ScriptInjector.TOP_WINDOW).inject();
	}

	@Override
	public void notifyOfErrors(List<ErrorDto> aErrors) {
		for (ErrorDto error : aErrors) {
			doNotify(ErrorUtils.formatError(error));
		}
	}

	private native void doNotify(String aError) /*-{
        $wnd.jQuery.bootstrapGrowl(aError, {
            type: 'danger',
			width: 300
        });
	}-*/;

}
