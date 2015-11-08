package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.History;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import net.dorokhov.pony.web.shared.UserDto;

import javax.inject.Inject;

public class AuthenticationListener extends AuthenticationManager.NoOpDelegate {

	private final TokenFormatter tokenFormatter;

	private final PlaceManager placeManager;

	@Inject
	public AuthenticationListener(TokenFormatter aTokenFormatter, PlaceManager aPlaceManager, AuthenticationManager aAuthenticationManager) {

		tokenFormatter = aTokenFormatter;
		placeManager = aPlaceManager;

		aAuthenticationManager.addDelegate(this);
	}

	@Override
	public void onAuthentication(UserDto aUser) {

		String historyToken = History.getToken();

		PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(historyToken);

		if (placeRequest.getNameToken().length() > 0) {
			placeManager.revealPlace(placeRequest);
		} else {
			placeManager.revealDefaultPlace();
		}
	}

	@Override
	public void onLogout(UserDto aUser) {
		placeManager.revealDefaultPlace();
	}

}
