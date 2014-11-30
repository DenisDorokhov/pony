package net.dorokhov.pony.web.security;

import net.dorokhov.pony.core.domain.UserTicket;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.InvalidTicketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthenticationFilter extends GenericFilterBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserService userService;

	private InstallationService installationService;

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

		HttpServletRequest httpRequest = (HttpServletRequest)aServletRequest;

		String token = httpRequest.getHeader("X-Auth-Token");

		UserTicket ticket = null;

		if (token != null && installationService.getInstallation() != null) {
			try {
				ticket = userService.validateTicket(token);
			} catch (InvalidTicketException e) {
				log.info("Ticket is invalid.", e);
			}
		}

		if (ticket != null) {

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(ticket.getUser(), null, ticket.getUser().getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

				SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		aFilterChain.doFilter(aServletRequest, aServletResponse);
	}
}
