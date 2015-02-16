package net.dorokhov.pony.web.client;

import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import net.dorokhov.pony.web.client.service.AuthenticationManager;

import javax.inject.Inject;

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
