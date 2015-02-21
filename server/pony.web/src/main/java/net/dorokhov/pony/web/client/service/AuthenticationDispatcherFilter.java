package net.dorokhov.pony.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

public class AuthenticationDispatcherFilter implements DispatcherFilter {

	private final SecurityStorage securityStorage;

	public AuthenticationDispatcherFilter() {
		securityStorage = SecurityStorage.INSTANCE;
	}

	@Override
	public boolean filter(Method aMethod, RequestBuilder aBuilder) {

		String token = securityStorage.getAccessToken();

		if (token != null) {
			aBuilder.setHeader("X-Access-Token", token);
		}

		return true;
	}

}
