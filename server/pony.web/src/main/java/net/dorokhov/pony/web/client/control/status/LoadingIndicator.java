package net.dorokhov.pony.web.client.control.status;

import net.dorokhov.pony.web.client.resource.Images;
import net.dorokhov.pony.web.client.resource.Messages;

public class LoadingIndicator extends BaseStatusIndicator {

	public LoadingIndicator() {
		setIcon(Images.INSTANCE.spinner());
		setText(Messages.INSTANCE.statusLoading());
	}

}
