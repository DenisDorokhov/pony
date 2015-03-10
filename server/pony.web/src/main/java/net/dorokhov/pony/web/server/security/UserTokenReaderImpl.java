package net.dorokhov.pony.web.server.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserTokenReaderImpl implements UserTokenReader {

	@Override
	public String readAccessToken(ServletRequest aRequest) {

		HttpServletRequest httpRequest = getHttpRequest(aRequest);

		if (httpRequest != null) {

			String token;

			token = httpRequest.getHeader("X-Access-Token");
			if (!StringUtils.isBlank(token)) {
				return token;
			}

			token = httpRequest.getParameter("x_access_token");
			if (!StringUtils.isBlank(token)) {
				return token;
			}

			if (!httpRequest.getServletPath().startsWith("/api/")) {
				token = getCookie(httpRequest, "Download-Access-Token");
				if (!StringUtils.isBlank(token)) {
					return token;
				}
			}
		}

		return null;
	}

	@Override
	public String readRefreshToken(ServletRequest aRequest) {

		HttpServletRequest httpRequest = getHttpRequest(aRequest);

		if (httpRequest != null) {

			String token = httpRequest.getHeader("X-Refresh-Token");

			if (!StringUtils.isBlank(token)) {
				return token;
			}
		}

		return null;
	}

	private HttpServletRequest getHttpRequest(ServletRequest aRequest) {

		if (aRequest instanceof HttpServletRequest) {
			return (HttpServletRequest) aRequest;
		}

		return null;
	}

	private String getCookie(HttpServletRequest aRequest, String aName) {

		for (Cookie cookie : aRequest.getCookies()) {
			if (cookie.getName().equals(aName)) {
				return cookie.getValue();
			}
		}

		return null;
	}

}
