package net.dorokhov.pony.web.server.security;

import javax.servlet.ServletRequest;

public interface UserTokenReader {

	public String readAccessToken(ServletRequest aRequest);

	public String readRefreshToken(ServletRequest aRequest);

}
