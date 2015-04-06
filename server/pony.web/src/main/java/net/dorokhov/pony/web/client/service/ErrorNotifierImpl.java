package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.List;

public class ErrorNotifierImpl implements ErrorNotifier {

	@Override
	public void notifyOfErrors(List<ErrorDto> aErrors) {
		for (ErrorDto error : aErrors) {
			doNotify(FormatUtils.formatError(error));
		}
	}

	private native void doNotify(String aError) /*-{
        $wnd.jQuery.bootstrapGrowl(aError, {
            type: 'danger',
			width: 300
        });
	}-*/;

}
