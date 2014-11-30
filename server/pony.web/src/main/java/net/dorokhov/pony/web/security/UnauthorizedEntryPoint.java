package net.dorokhov.pony.web.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest aRequest, HttpServletResponse aResponse, AuthenticationException aAuthException) throws IOException, ServletException {
		aResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
