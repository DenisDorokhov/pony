package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.util.ErrorUtils;
import net.dorokhov.pony.web.shared.ErrorDto;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlHelper;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlOptions;

import javax.inject.Inject;
import java.util.List;

public class ErrorNotifierImpl implements ErrorNotifier {

	private final GrowlOptions growlOptions;

	@Inject
	public ErrorNotifierImpl() {
		growlOptions = GrowlHelper.getNewOptions();
		growlOptions.setDangerType();
	}

	@Override
	public void notifyOfErrors(List<ErrorDto> aErrors) {
		for (ErrorDto error : aErrors) {
			Growl.growl(ErrorUtils.formatError(error), growlOptions);
		}
	}

}
