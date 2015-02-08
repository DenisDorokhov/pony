package net.dorokhov.pony.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.Inject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

public class AuthenticationDispatcherFilter implements DispatcherFilter {

	private final AuthenticationStatus authenticationStatus;

	@Inject
	public AuthenticationDispatcherFilter(AuthenticationStatus aAuthenticationStatus) {
		authenticationStatus = aAuthenticationStatus;
	}

	@Override
	public boolean filter(Method aMethod, RequestBuilder aBuilder) {

		if (authenticationStatus.isAuthenticated()) {
			aBuilder.setHeader("X-Auth-Token", authenticationStatus.getToken());
		}

		return true;
	}

}
