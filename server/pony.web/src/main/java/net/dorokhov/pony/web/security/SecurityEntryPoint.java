package net.dorokhov.pony.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dorokhov.pony.web.domain.ErrorDto;
import net.dorokhov.pony.web.domain.ResponseDto;
import net.dorokhov.pony.web.service.ResponseBuilder;
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

	private String jsonResponsePathPrefix;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	public void setJsonResponsePathPrefix(String aJsonResponsePathPrefix) {
		jsonResponsePathPrefix = aJsonResponsePathPrefix;
	}

	@Override
	public void commence(HttpServletRequest aRequest, HttpServletResponse aResponse, AuthenticationException aException) throws IOException, ServletException {

		log.warn("Access denied to [" + aRequest.getServletPath() + "].");

		if (jsonResponsePathPrefix != null && aRequest.getServletPath().startsWith(jsonResponsePathPrefix)) {

			ResponseDto error = responseBuilder.build(new ErrorDto("errorAccessDenied", aException.getMessage()));

			aResponse.setContentType("application/json");
			aResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			aResponse.getOutputStream().print(objectMapper.writeValueAsString(error));

		} else {
			aResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
