package net.dorokhov.pony.web.client.control;

import net.dorokhov.pony.web.client.resource.Images;
import net.dorokhov.pony.web.client.resource.Messages;

public class StatusIndicatorError extends StatusIndicator {

	public StatusIndicatorError() {
		setIcon(Images.INSTANCE.error());
		setText(Messages.INSTANCE.statusError());
	}

}
