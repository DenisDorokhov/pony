package net.dorokhov.pony.web.server.security;

import javax.servlet.ServletRequest;

public interface UserTokenReader {

	public String readToken(ServletRequest aRequest);

}
