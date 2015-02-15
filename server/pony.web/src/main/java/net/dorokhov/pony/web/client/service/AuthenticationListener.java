package net.dorokhov.pony.web.client.service;

import com.gwtplatform.mvp.client.proxy.PlaceManager;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;

public class AuthenticationListener extends AuthenticationManager.NoOpDelegate {

	private final PlaceManager placeManager;

	@Inject
	public AuthenticationListener(PlaceManager aPlaceManager, AuthenticationManager aAuthenticationManager) {

		placeManager = aPlaceManager;

		aAuthenticationManager.addDelegate(this);
	}

	@Override
	public void onAuthentication(UserDto aUser) {
		placeManager.revealDefaultPlace();
	}

	@Override
	public void onLogout(UserDto aUser, boolean aExplicit) {
		placeManager.revealDefaultPlace();
	}

}
