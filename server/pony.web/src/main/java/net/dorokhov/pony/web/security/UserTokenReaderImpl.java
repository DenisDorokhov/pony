package net.dorokhov.pony.web.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserTokenReaderImpl implements UserTokenReader {

	@Override
	public String readToken(ServletRequest aRequest) {

		if (aRequest instanceof HttpServletRequest) {

			HttpServletRequest httpRequest = (HttpServletRequest) aRequest;

			String token;

			token = httpRequest.getHeader("X-Auth-Token");
			if (!StringUtils.isBlank(token)) {
				return token;
			}

			token = httpRequest.getParameter("x_auth_token");
			if (!StringUtils.isBlank(token)) {
				return token;
			}
		}

		return null;
	}
}
