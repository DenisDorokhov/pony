package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.exception.*;

import java.util.Date;
import java.util.List;

public interface UserService {

	public interface Authentication {

		public String getAccessToken();

		public Date getAccessTokenExpiration();

		public String getRefreshToken();

		public Date getRefreshTokenExpiration();

		public User getUser();
	}

	public User getById(Long aId);
	public User getByEmail(String aEmail);

	public List<User> getAll();

	public User create(User aUser) throws UserExistsException;
	public User update(User aUser, String aNewPassword) throws UserNotFoundException, UserExistsException;

	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException;

	public Authentication authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public void authenticateToken(String aToken) throws InvalidTokenException;
	public Authentication refreshToken(String aRefreshToken) throws InvalidTokenException;

	public User logout(String aToken) throws InvalidTokenException;

	public User getAuthenticatedUser() throws NotAuthenticatedException;

	public User updateAuthenticatedUser(User aUser, String aOldPassword, String aNewPassword) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException;

	public void cleanTokens();
}
