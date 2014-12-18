package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.exception.*;

import java.util.List;

public interface UserService {

	public User getById(Long aId);
	public User getByEmail(String aEmail);

	public List<User> getAll();

	public User create(User aUser) throws UserExistsException;
	public User update(User aUser, String aNewPassword) throws UserNotFoundException, UserExistsException;

	public String authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public void authenticate(String aToken) throws InvalidTokenException;

	public void logout(String aToken) throws InvalidTokenException;

	public User getAuthenticatedUser() throws NotAuthenticatedException;

	public User updateAuthenticatedUser(User aUser, String aOldPassword, String aNewPassword) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException;

	public void cleanTickets();
}
