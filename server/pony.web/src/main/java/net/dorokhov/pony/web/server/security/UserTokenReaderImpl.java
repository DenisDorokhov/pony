package net.dorokhov.pony.web.server.security;

import net.dorokhov.pony.web.shared.SecurityTokens;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Service
public class UserTokenReaderImpl implements UserTokenReader {

	@Override
	public String readAccessToken(ServletRequest aRequest) {

		HttpServletRequest httpRequest = getHttpRequest(aRequest);

		if (httpRequest != null) {

			String token;

			token = httpRequest.getHeader(SecurityTokens.ACCESS_TOKEN_HEADER);
			if (!StringUtils.isBlank(token)) {
				return token;
			}

			token = httpRequest.getParameter(SecurityTokens.ACCESS_TOKEN_PARAM);
			if (!StringUtils.isBlank(token)) {
				return token;
			}

			if (!httpRequest.getServletPath().startsWith("/api/")) {
				token = decodeBase64Value(getCookie(httpRequest, SecurityTokens.ACCESS_TOKEN_COOKIE));
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

			String token = httpRequest.getHeader(SecurityTokens.REFRESH_TOKEN_HEADER);

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

		Cookie cookie = WebUtils.getCookie(aRequest, aName);

		if (cookie != null) {
			try {
				return URLDecoder.decode(cookie.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {}
		}

		return null;
	}

	private String decodeBase64Value(String aValue) {

		if (aValue != null) {
			try {
				return new String(Base64.decodeBase64(aValue), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {}
		}

		return null;
	}

}
