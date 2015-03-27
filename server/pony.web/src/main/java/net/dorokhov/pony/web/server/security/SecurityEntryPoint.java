package net.dorokhov.pony.web.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dorokhov.pony.web.server.service.ResponseBuilder;
import net.dorokhov.pony.web.shared.ErrorCodes;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ObjectMapper objectMapper = new ObjectMapper();

	private ResponseBuilder responseBuilder;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@Override
	public void commence(HttpServletRequest aRequest, HttpServletResponse aResponse, AuthenticationException aException) throws IOException, ServletException {

		log.debug("Access denied to [" + aRequest.getServletPath() + "].");

		if (aRequest.getServletPath().startsWith("/api/")) {

			ResponseDto error = responseBuilder.build(new ErrorDto(ErrorCodes.ACCESS_DENIED, "Access denied."));

			aResponse.setContentType("application/json");
			aResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			aResponse.getOutputStream().print(objectMapper.writeValueAsString(error));

		} else {
			aResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
