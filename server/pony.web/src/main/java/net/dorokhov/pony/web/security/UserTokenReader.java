package net.dorokhov.pony.web.security;

import net.dorokhov.pony.core.domain.UserToken;

import javax.servlet.ServletRequest;

public interface UserTokenReader {

	public UserToken readToken(ServletRequest aRequest);

}
