package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserToken;
import net.dorokhov.pony.core.user.exception.*;

import java.util.List;

public interface UserService {

	public User getById(Long aId);

	public List<User> getAll();

	public User create(User aUser) throws UserExistsException;
	public User update(User aUser, String aNewPassword) throws UserNotFoundException, UserExistsException;

	public UserToken authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public void authenticate(UserToken aToken) throws InvalidTokenException;

	public void logout(UserToken aToken) throws InvalidTokenException;

	public User getAuthenticatedUser() throws NotAuthenticatedException;

	public User updateAuthenticatedUser(User aUser, String aOldPassword, String aNewPassword) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidCredentialsException, UserNotFoundException, UserExistsException;

	public void cleanTickets();
}
