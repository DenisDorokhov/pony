package net.dorokhov.pony.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

public class AuthenticationDispatcherFilter implements DispatcherFilter {

	private final AuthenticationStorage authenticationStorage;

	public AuthenticationDispatcherFilter() {
		authenticationStorage = AuthenticationStorage.INSTANCE;
	}

	@Override
	public boolean filter(Method aMethod, RequestBuilder aBuilder) {

		String token = authenticationStorage.getAccessToken();

		if (token != null) {
			aBuilder.setHeader("X-Access-Token", token);
		}

		return true;
	}

}
