package net.dorokhov.pony.web.client.service;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

@DefaultGatekeeper
public class UserGatekeeper implements Gatekeeper {

	private final AuthenticationManager authenticationManager;

	@Inject
	public UserGatekeeper(AuthenticationManager aAuthenticationManager) {
		authenticationManager = aAuthenticationManager;
	}

	@Override
	public boolean canReveal() {
		return authenticationManager.isAuthenticated();
	}

}
