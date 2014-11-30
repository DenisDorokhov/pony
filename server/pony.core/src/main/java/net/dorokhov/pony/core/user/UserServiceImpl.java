package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.dao.UserDao;
import net.dorokhov.pony.core.dao.UserTicketDao;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserTicket;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.user.exception.InvalidCredentialsException;
import net.dorokhov.pony.core.user.exception.InvalidTicketException;
import net.dorokhov.pony.core.user.exception.UserExistsException;
import net.dorokhov.pony.core.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private UserDao userDao;

	private UserTicketDao userTicketDao;

	private InstallationService installationService;

	private PasswordEncoder passwordEncoder;

	private int ticketLifetime;

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

	@Value("${ticket.lifetime}")
	public void setTicketLifetime(int aTicketLifetime) {
		ticketLifetime = aTicketLifetime;
	}

	@Override
	public UserDetails loadUserByUsername(String aUsername) throws UsernameNotFoundException {

		User user = userDao.findByUsername(aUsername);

		if (user == null) {
			throw new UsernameNotFoundException("User [" + aUsername + "] could not be found.");
		}

		return user;
	}

	@Override
	@Transactional
	public UserTicket authenticate(String aEmail, String aPassword) throws InvalidCredentialsException {

		User user = userDao.findByUsernameAndPassword(aEmail, aPassword);

		if (user == null) {
			throw new InvalidCredentialsException();
		}

		UserTicket ticket = new UserTicket();

		ticket.setId(UUID.randomUUID().toString());
		ticket.setUser(user);

		return userTicketDao.save(ticket);
	}

	@Override
	@Transactional
	public UserTicket validateTicket(String aId) throws InvalidTicketException {

		UserTicket ticket = userTicketDao.findOne(aId);

		if (ticket == null) {
			throw new InvalidTicketException(aId);
		}

		Date ticketDate = ticket.getUpdateDate();
		if (ticketDate == null) {
			ticketDate = ticket.getCreationDate();
		}

		long ticketAge = (new Date().getTime() - ticketDate.getTime()) / 1000;

		if (ticketAge > ticketLifetime) {

			userTicketDao.delete(ticket);

			throw new InvalidTicketException(aId);
		}

		return userTicketDao.save(ticket);
	}

	@Override
	@Transactional
	public User create(User aUser) throws UserExistsException {

		if (aUser.getId() != null) {
			throw new IllegalArgumentException("User identifier must be null.");
		}

		synchronized (lock) {

			if (userDao.findByUsername(aUser.getUsername()) != null) {
				throw new UserExistsException(aUser.getUsername());
			}

			aUser.setPassword(passwordEncoder.encode(aUser.getPassword()));

			return userDao.save(aUser);
		}
	}

	@Override
	@Transactional
	public User update(User aUser, boolean aUpdatePassword) throws UserExistsException, UserNotFoundException {

		if (aUser.getId() != null) {
			throw new IllegalArgumentException("User identifier must not be null.");
		}

		synchronized (lock) {

			User currentUser = userDao.findOne(aUser.getId());

			if (currentUser == null) {
				throw new UserNotFoundException(aUser.getId());
			}

			User existingUser = userDao.findByUsername(aUser.getUsername());

			if (!existingUser.getId().equals(aUser.getId())) {
				throw new UserExistsException(aUser.getUsername());
			}

			if (aUpdatePassword) {
				aUser.setPassword(passwordEncoder.encode(aUser.getPassword()));
			} else {
				aUser.setPassword(currentUser.getPassword());
			}

			return userDao.save(aUser);
		}
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
