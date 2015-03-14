package net.dorokhov.pony.web.client.control;

import net.dorokhov.pony.web.client.resource.Images;
import net.dorokhov.pony.web.client.resource.Messages;

public class StatusIndicatorLoading extends StatusIndicator {

	public StatusIndicatorLoading() {
		setIcon(Images.INSTANCE.spinner());
		setText(Messages.INSTANCE.statusLoading());
	}

}
