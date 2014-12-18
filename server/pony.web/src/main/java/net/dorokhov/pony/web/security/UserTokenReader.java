package net.dorokhov.pony.web.security;

import javax.servlet.ServletRequest;

public interface UserTokenReader {

	public String readToken(ServletRequest aRequest);

}
