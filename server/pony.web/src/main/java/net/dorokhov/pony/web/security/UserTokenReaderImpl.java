package net.dorokhov.pony.web.security;

import net.dorokhov.pony.core.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserTokenReaderImpl implements UserTokenReader {

	@Override
	public UserToken readToken(ServletRequest aRequest) {

		if (aRequest instanceof HttpServletRequest) {

			HttpServletRequest httpRequest = (HttpServletRequest) aRequest;

			String token = httpRequest.getHeader("X-Auth-Token");

			if (!StringUtils.isBlank(token)) {
				return new UserToken(token);
			}
		}

		return null;
	}
}
