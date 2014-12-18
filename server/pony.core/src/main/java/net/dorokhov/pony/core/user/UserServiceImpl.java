package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.dao.UserDao;
import net.dorokhov.pony.core.dao.UserTicketDao;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserTicket;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.security.UserDetailsImpl;
import net.dorokhov.pony.core.user.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserDao userDao;

	private UserTicketDao userTicketDao;

	private InstallationService installationService;

	private PasswordEncoder passwordEncoder;

	private AuthenticationManager authenticationManager;

	private int ticketLifetime;

	private String debugToken;

	@Autowired
	public void setUserDao(UserDao aUserDao) {
		userDao = aUserDao;
	}

	@Autowired
	public void setUserTicketDao(UserTicketDao aUserTicketDao) {
		userTicketDao = aUserTicketDao;
	}

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder aPasswordEncoder) {
		passwordEncoder = aPasswordEncoder;
	}

	@Autowired
	@Qualifier("authenticationManager")
	public void setAuthenticationManager(AuthenticationManager aAuthenticationManager) {
		authenticationManager = aAuthenticationManager;
	}

	@Value("${user.ticketLifetime}")
	public void setTicketLifetime(int aTicketLifetime) {
		ticketLifetime = aTicketLifetime;
	}

	@Value("${user.debugToken}")
	public void setDebugToken(String aDebugToken) {
		debugToken = aDebugToken;
	}

	@Override
	@Transactional(readOnly = true)
	public User getById(Long aId) {
		return userDao.findOne(aId);
	}

	@Override
	@Transactional(readOnly = true)
	public User getByEmail(String aEmail) {
		return userDao.findByEmail(aEmail);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAll() {

		List<User> userList = new ArrayList<>();

		for (User user : userDao.findAll()) {
			userList.add(user);
		}

		return userList;
	}

	@Override
	@Transactional
	public User create(User aUser) throws UserExistsException {

		if (aUser.getId() != null) {
			throw new IllegalArgumentException("User identifier must be null.");
		}

		if (getByEmail(aUser.getEmail()) != null) {
			throw new UserExistsException(aUser.getEmail());
		}

		aUser.setPassword(passwordEncoder.encode(aUser.getPassword()));

		return userDao.save(aUser);
	}

	@Override
	@Transactional
	public User update(User aUser, String aNewPassword) throws UserNotFoundException, UserExistsException {

		if (aUser.getId() == null) {
			throw new IllegalArgumentException("User identifier must be null.");
		}

		User currentUser = userDao.findOne(aUser.getId());

		if (currentUser == null) {
			throw new UserNotFoundException(aUser.getId());
		}

		User existingUser = getByEmail(aUser.getEmail());

		if (!existingUser.getId().equals(aUser.getId())) {
			throw new UserExistsException(aUser.getEmail());
		}

		if (aNewPassword != null) {
			aUser.setPassword(passwordEncoder.encode(aNewPassword));
		} else {
			aUser.setPassword(existingUser.getPassword());
		}

		User updatedUser = userDao.save(aUser);

		User authenticatedUser = null;
		try {
			authenticatedUser = getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			// User is not authenticated
		}

		if (authenticatedUser != null) {
			if (updatedUser.getId().equals(authenticatedUser.getId())) {

				UserDetailsImpl userDetails = new UserDetailsImpl(updatedUser);

				Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(token);
			}
		}

		return updatedUser;
	}

	@Override
	@Transactional
	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException {

		User currentUser = userDao.findOne(aId);

		if (currentUser == null) {
			throw new UserNotFoundException(aId);
		}

		User authenticatedUser = null;
		try {
			authenticatedUser = getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			// User is not authenticated
		}

		if (authenticatedUser != null) {
			if (currentUser.getId().equals(authenticatedUser.getId())) {
				throw new UserSelfDeletionException(aId);
			}
		}

		userTicketDao.deleteByUserId(aId);
		userDao.delete(aId);
	}

	@Override
	@Transactional
	public String authenticate(String aEmail, String aPassword) throws InvalidCredentialsException {

		Authentication authentication = new UsernamePasswordAuthenticationToken(aEmail, aPassword);

		try {
			authentication = authenticationManager.authenticate(authentication);
		} catch (AuthenticationException e) {
			throw new InvalidCredentialsException();
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String token = UUID.randomUUID().toString();

		UserTicket ticket = new UserTicket();

		ticket.setId(token);
		ticket.setUser(userDetails.getUser());

		ticket = userTicketDao.save(ticket);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		log.info("User [" + ticket.getUser().getEmail() + "] has authenticated.");

		return token;
	}

	@Override
	@Transactional
	public void authenticate(String aToken) throws InvalidTokenException {

		User user;

		if (!StringUtils.isBlank(debugToken) && Objects.equals(aToken, debugToken)) {

			Page<User> page = userDao.findAll(new PageRequest(0, 1, new Sort("id")));
			if (page.getNumberOfElements() > 0) {
				user = page.getContent().get(0);
			} else {
				throw new RuntimeException("No users found in the database.");
			}

		} else {

			UserTicket ticket = userTicketDao.findOne(aToken);

			if (ticket == null) {
				throw new InvalidTokenException();
			}

			Date ticketDate = ticket.getUpdateDate();
			if (ticketDate == null) {
				ticketDate = ticket.getCreationDate();
			}

			long ticketAge = (new Date().getTime() - ticketDate.getTime()) / 1000;

			if (ticketAge > ticketLifetime) {
				throw new InvalidTokenException();
			}

			ticket = userTicketDao.save(ticket);

			user = ticket.getUser();
		}

		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(token);
	}

	@Override
	@Transactional
	public void logout(String aToken) throws InvalidTokenException {

		UserTicket ticket = userTicketDao.findOne(aToken);

		if (ticket == null) {
			throw new InvalidTokenException();
		}

		userTicketDao.delete(ticket);

		SecurityContextHolder.clearContext();

		log.info("User [" + ticket.getUser().getEmail() + "] has logged out.");
	}

	@Override
	@Transactional
	public User getAuthenticatedUser() throws NotAuthenticatedException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			throw new NotAuthenticatedException();
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return userDetails.getUser();
	}

	@Override
	@Transactional
	public User updateAuthenticatedUser(User aUser, String aOldPassword, String aNewPassword) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException {

		User authenticatedUser = getAuthenticatedUser();

		if (!authenticatedUser.getId().equals(aUser.getId())) {
			throw new NotAuthorizedException("Cannot update user which is not currently authenticated.");
		}
		if (!passwordEncoder.matches(aOldPassword, authenticatedUser.getPassword())) {
			throw new InvalidPasswordException();
		}

		return update(aUser, aNewPassword);
	}

	@Override
	@Transactional
	@Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
	public void cleanTickets() {
		if (installationService.getInstallation() != null) {

			log.debug("cleaning tickets...");

			Date maxDate = new Date(new Date().getTime() - ticketLifetime * 1000);

			userTicketDao.deleteByUpdateDateNullAndCreationDateLessThan(maxDate);
			userTicketDao.deleteByUpdateDateNotNullAndUpdateDateLessThan(maxDate);
		}
	}
}
