package net.dorokhov.pony.web.server.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.net.HttpCookie;
import java.net.URLDecoder;

@Service
public class UserTokenReaderImpl implements UserTokenReader {

	private final Logger log = LoggerFactory.getLogger(getClass());

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

		// Parse cookies manually to avoid incorrect cookie values under Apache Tomcat

		String cookieHeader = aRequest.getHeader("Cookie");

		if (cookieHeader != null) {
			for (String cookieItem : cookieHeader.split(";")) {
				if (!StringUtils.isEmpty(cookieItem)) {
					try {
						for (HttpCookie cookie : HttpCookie.parse(cookieItem)) {
							if (cookie.getName().equals(aName)) {
								return URLDecoder.decode(cookie.getValue(), "UTF-8");
							}
						}
					} catch (Exception e) {
						log.warn("Could not parse cookie: " + cookieItem, e);
					}
				}
			}
		}

		return null;
	}

}
