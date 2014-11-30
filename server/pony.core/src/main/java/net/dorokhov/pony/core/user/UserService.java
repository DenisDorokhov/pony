package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserTicket;
import net.dorokhov.pony.core.user.exception.InvalidCredentialsException;
import net.dorokhov.pony.core.user.exception.InvalidTicketException;
import net.dorokhov.pony.core.user.exception.UserExistsException;
import net.dorokhov.pony.core.user.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

	public UserTicket authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public UserTicket validateTicket(String aId) throws InvalidTicketException;

	public User create(User aUser) throws UserExistsException;
	public User update(User aUser, boolean aUpdatePassword) throws UserExistsException, UserNotFoundException;

	public void cleanTickets();
}
