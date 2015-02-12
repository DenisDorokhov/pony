package net.dorokhov.pony.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.Inject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

public class AuthenticationDispatcherFilter implements DispatcherFilter {

	private final AuthenticationManager authenticationManager;

	@Inject
	public AuthenticationDispatcherFilter(AuthenticationManager aAuthenticationManager) {
		authenticationManager = aAuthenticationManager;
	}

	@Override
	public boolean filter(Method aMethod, RequestBuilder aBuilder) {

		String token = authenticationManager.getAccessToken();

		if (token != null) {
			aBuilder.setHeader("X-Auth-Token", token);
		}

		return true;
	}

}
