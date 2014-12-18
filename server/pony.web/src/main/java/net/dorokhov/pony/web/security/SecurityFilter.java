package net.dorokhov.pony.web.security;

import net.dorokhov.pony.core.domain.UserToken;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter extends GenericFilterBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserTokenReader userTokenReader;

	private UserService userService;

	private InstallationService installationService;

	@Autowired
	public void setUserTokenReader(UserTokenReader aUserTokenReader) {
		userTokenReader = aUserTokenReader;
	}

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Override
	@Transactional
	public void doFilter(ServletRequest aServletRequest, ServletResponse aServletResponse, FilterChain aFilterChain) throws IOException, ServletException {

		UserToken token = userTokenReader.readToken(aServletRequest);

		if (token != null && installationService.getInstallation() != null) {
			try {
				userService.authenticate(token);
			} catch (InvalidTokenException e) {
				log.info("Token [" + token + "] is invalid.");
			}
		}

		aFilterChain.doFilter(aServletRequest, aServletResponse);
	}
}
