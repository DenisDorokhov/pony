package net.dorokhov.pony.web.client.service;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import net.dorokhov.pony.web.shared.RoleDto;

@DefaultGatekeeper
public class UserGatekeeper implements Gatekeeper {

	private final AuthenticationStatus authenticationStatus;

	@Inject
	public UserGatekeeper(AuthenticationStatus aAuthenticationStatus) {
		authenticationStatus = aAuthenticationStatus;
	}

	@Override
	public boolean canReveal() {
		return authenticationStatus.isAuthenticated() && (
				authenticationStatus.getCurrentUser().getRole().equals(RoleDto.USER) ||
						authenticationStatus.getCurrentUser().getRole().equals(RoleDto.ADMIN));
	}

}
