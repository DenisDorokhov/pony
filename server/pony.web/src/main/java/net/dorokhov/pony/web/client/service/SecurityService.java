package net.dorokhov.pony.web.client.service;

import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

@DefaultGatekeeper
public class SecurityService implements Gatekeeper {

	@Override
	public boolean canReveal() {
		return true;
	}

}
