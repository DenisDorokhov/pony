package net.dorokhov.pony.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import net.dorokhov.pony.web.shared.SecurityTokens;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import javax.inject.Inject;

public class AuthenticationDispatcherFilter implements DispatcherFilter {

	private final SecurityStorage securityStorage;

	@Inject
	public AuthenticationDispatcherFilter(SecurityStorage aSecurityStorage) {
		securityStorage = aSecurityStorage;
	}

	@Override
	public boolean filter(Method aMethod, RequestBuilder aBuilder) {

		String token = securityStorage.getAccessToken();

		if (token != null) {
			aBuilder.setHeader(SecurityTokens.ACCESS_TOKEN_HEADER, token);
		}

		return true;
	}

}
