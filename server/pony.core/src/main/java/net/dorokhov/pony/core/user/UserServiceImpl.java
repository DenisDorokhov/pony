package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.dao.AccessTokenDao;
import net.dorokhov.pony.core.dao.RefreshTokenDao;
import net.dorokhov.pony.core.dao.UserDao;
import net.dorokhov.pony.core.domain.AccessToken;
import net.dorokhov.pony.core.domain.RefreshToken;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.common.BaseToken;
import net.dorokhov.pony.core.security.UserDetailsImpl;
import net.dorokhov.pony.core.user.exception.*;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TransactionTemplate transactionTemplate;

	private UserDao userDao;

	private AccessTokenDao accessTokenDao;
	private RefreshTokenDao refreshTokenDao;

	private PasswordEncoder passwordEncoder;

	private AuthenticationManager authenticationManager;

	private long accessTokenLifetime;
	private long refreshTokenLifetime;

	private String debugToken;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Autowired
	public void setUserDao(UserDao aUserDao) {
		userDao = aUserDao;
	}

	@Autowired
	public void setAccessTokenDao(AccessTokenDao aAccessTokenDao) {
		accessTokenDao = aAccessTokenDao;
	}

	@Autowired
	public void setRefreshTokenDao(RefreshTokenDao aRefreshTokenDao) {
		refreshTokenDao = aRefreshTokenDao;
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

	@Value("${user.accessTokenLifetime}")
	public void setAccessTokenLifetime(long aAccessTokenLifetime) {
		accessTokenLifetime = aAccessTokenLifetime;
	}

	@Value("${user.refreshTokenLifetime}")
	public void setRefreshTokenLifetime(long aRefreshTokenLifetime) {
		refreshTokenLifetime = aRefreshTokenLifetime;
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
	public Page<User> getAll(Pageable aPageable) {
		return userDao.findAll(aPageable);
	}

	@Override
	@Transactional(rollbackFor = {UserExistsException.class})
	public User create(User aUser) throws UserExistsException {

		if (aUser.getId() != null) {
			throw new IllegalArgumentException("User identifier must be null.");
		}

		String email = aUser.getEmail() != null ? aUser.getEmail().trim() : null;

		if (email != null && getByEmail(email) != null) {
			throw new UserExistsException(email);
		}

		aUser.setPassword(passwordEncoder.encode(aUser.getPassword()));

		return userDao.save(aUser);
	}

	@Override
	@Transactional(rollbackFor = {UserNotFoundException.class, UserExistsException.class, SelfRoleModificationException.class})
	public User update(final User aUser, String aNewPassword) throws UserNotFoundException, UserExistsException, SelfRoleModificationException {

		if (aUser.getId() == null) {
			throw new IllegalArgumentException("User identifier must be null.");
		}

		User currentUser = getById(aUser.getId());

		if (currentUser == null) {
			throw new UserNotFoundException(aUser.getId());
		}

		User sameEmailUser = getByEmail(aUser.getEmail());

		if (sameEmailUser != null && !sameEmailUser.getId().equals(aUser.getId())) {
			throw new UserExistsException(aUser.getEmail());
		}

		if (aNewPassword != null) {
			aUser.setPassword(passwordEncoder.encode(aNewPassword));
		} else {
			aUser.setPassword(currentUser.getPassword());
		}

		boolean rolesModified = false;

		// Avoid using first-level cache to check changes in property values
		User storedUser = transactionTemplate.execute(new TransactionCallback<User>() {
			@Override
			public User doInTransaction(TransactionStatus status) {
				return getById(aUser.getId());
			}
		});
		if (storedUser != null && !ListUtils.isEqualList(aUser.getRoles(), storedUser.getRoles())) {

			rolesModified = true;

			aUser.setUpdateDate(new Date()); // @PreUpdate is not called when changing @ElementCollection contents
		}

		User authenticatedUser = null;
		try {
			authenticatedUser = getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			// User is not authenticated
		}

		if (authenticatedUser != null && aUser.getId().equals(authenticatedUser.getId()) && rolesModified) {
			throw new SelfRoleModificationException(aUser.getId());
		}

		User updatedUser = userDao.save(aUser);

		if (authenticatedUser != null && updatedUser.getId().equals(authenticatedUser.getId())) {

			UserDetailsImpl userDetails = new UserDetailsImpl(updatedUser);

			org.springframework.security.core.Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(token);
		}

		return updatedUser;
	}

	@Override
	@Transactional
	public void delete(Long aId) throws UserNotFoundException, SelfDeletionException {

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
				throw new SelfDeletionException(aId);
			}
		}

		accessTokenDao.deleteByUserId(aId);
		refreshTokenDao.deleteByUserId(aId);
		userDao.delete(aId);
	}

	@Override
	@Transactional
	public Authentication authenticate(String aEmail, String aPassword) throws InvalidCredentialsException {

		org.springframework.security.core.Authentication springAuthentication = new UsernamePasswordAuthenticationToken(aEmail, aPassword);

		try {
			springAuthentication = authenticationManager.authenticate(springAuthentication);
		} catch (AuthenticationException e) {
			throw new InvalidCredentialsException();
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) springAuthentication.getPrincipal();

		TokenString accessTokenString = new TokenString();
		AccessToken accessToken = createAccessToken(accessTokenString, userDetails.getUser());

		TokenString refreshTokenString = new TokenString();
		RefreshToken refreshToken = createRefreshToken(refreshTokenString, userDetails.getUser());

		SecurityContextHolder.getContext().setAuthentication(springAuthentication);

		log.debug("User [" + userDetails.getUser().getEmail() + "] has authenticated with email and password.");

		AuthenticationImpl authentication = new AuthenticationImpl();

		authentication.setAccessToken(accessTokenString.toString());
		authentication.setRefreshToken(refreshTokenString.toString());

		authentication.setAccessTokenExpiration(new Date(accessToken.getDate().getTime() + accessTokenLifetime * 1000));
		authentication.setRefreshTokenExpiration(new Date(refreshToken.getDate().getTime() + refreshTokenLifetime * 1000));

		authentication.setUser(userDetails.getUser());

		return authentication;
	}

	@Override
	@Transactional
	public void authenticateToken(String aToken) throws InvalidTokenException {

		User user;

		if (!StringUtils.isBlank(debugToken) && Objects.equals(aToken, debugToken)) {

			Page<User> page = userDao.findAll(new PageRequest(0, 1, new Sort("id")));
			if (page.hasContent()) {
				user = page.getContent().get(0);
			} else {
				throw new RuntimeException("No users found in the database.");
			}

		} else {

			AccessToken token = getAccessTokenByString(aToken);

			validateTokenAge(token, accessTokenLifetime);

			user = token.getUser();
		}

		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		org.springframework.security.core.Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(token);

		log.trace("User [" + userDetails.getUser().getEmail() + "] has authenticated with access token.");
	}

	@Override
	@Transactional
	public Authentication refreshToken(String aRefreshToken) throws InvalidTokenException {

		RefreshToken token = getRefreshTokenByString(aRefreshToken);

		validateTokenAge(token, refreshTokenLifetime);

		User user = token.getUser();

		refreshTokenDao.delete(token);

		TokenString accessTokenString = new TokenString();
		AccessToken accessToken = createAccessToken(accessTokenString, user);

		TokenString refreshTokenString = new TokenString();
		RefreshToken refreshToken = createRefreshToken(refreshTokenString, user);

		log.debug("Token for user [" + user.getEmail() + "] has been refreshed.");

		AuthenticationImpl authentication = new AuthenticationImpl();

		authentication.setAccessToken(accessTokenString.toString());
		authentication.setRefreshToken(refreshTokenString.toString());

		authentication.setAccessTokenExpiration(getTokenExpiration(accessToken));
		authentication.setRefreshTokenExpiration(getTokenExpiration(refreshToken));

		authentication.setUser(user);

		return authentication;
	}

	@Override
	@Transactional
	public User logout(String aToken) throws InvalidTokenException {

		AccessToken token = getAccessTokenByString(aToken);

		User user = token.getUser();

		accessTokenDao.delete(token);

		SecurityContextHolder.clearContext();

		log.debug("User [" + user.getEmail() + "] has logged out.");

		return user;
	}

	@Override
	@Transactional
	public User getAuthenticatedUser() throws NotAuthenticatedException {

		org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			throw new NotAuthenticatedException();
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return userDetails.getUser();
	}

	@Override
	@Transactional(rollbackFor = {NotAuthenticatedException.class, NotAuthorizedException.class, InvalidPasswordException.class, UserNotFoundException.class, UserExistsException.class, SelfRoleModificationException.class})
	public User updateAuthenticatedUser(User aUser, String aOldPassword, String aNewPassword) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException, SelfRoleModificationException {

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
	public void cleanTokens() {

		log.debug("Cleaning tokens...");

		Date maxAccessTokenDate = new Date(new Date().getTime() - accessTokenLifetime * 1000);

		Long deletedAccessTokens = accessTokenDao.deleteByDateLessThan(maxAccessTokenDate);
		if (deletedAccessTokens > 0) {
			log.debug("Deleted [" + deletedAccessTokens + "] access tokens.");
		}

		Date maxRefreshTokenDate = new Date(new Date().getTime() - refreshTokenLifetime * 1000);

		Long deletedRefreshTokens = refreshTokenDao.deleteByDateLessThan(maxRefreshTokenDate);
		if (deletedRefreshTokens > 0) {
			log.debug("Deleted [" + deletedRefreshTokens + "] refresh tokens.");
		}
	}

	private AccessToken createAccessToken(TokenString aToken, User aUser) {

		AccessToken token = new AccessToken();

		token.setId(aToken.getTokenId());
		token.setSecret(passwordEncoder.encode(aToken.getTokenSecret()));
		token.setUser(aUser);

		return accessTokenDao.save(token);
	}

	private RefreshToken createRefreshToken(TokenString aToken, User aUser) {

		RefreshToken token = new RefreshToken();

		token.setId(aToken.getTokenId());
		token.setSecret(passwordEncoder.encode(aToken.getTokenSecret()));
		token.setUser(aUser);

		return refreshTokenDao.save(token);
	}

	private void validateTokenAge(BaseToken aToken, long aLifetime) throws InvalidTokenException {

		long tokenAge = (new Date().getTime() - aToken.getDate().getTime()) / 1000;

		if (tokenAge > aLifetime) {

			log.trace("Token is too old [" + aToken + "], age is [" + tokenAge + "], lifetime is [" + aLifetime + "].");

			throw new InvalidTokenException();
		}
	}

	private AccessToken getAccessTokenByString(String aTokenString) throws InvalidTokenException {

		TokenString tokenString = TokenString.valueOf(aTokenString);

		AccessToken token = accessTokenDao.findOne(tokenString.getTokenId());

		if (token == null) {

			log.debug("Access token not found [" + aTokenString + "].");

			throw new InvalidTokenException();
		}

		if (!passwordEncoder.matches(tokenString.getTokenSecret(), token.getSecret())) {

			log.debug("Access token secret does not match [" + aTokenString + "].");

			throw new InvalidTokenException();
		}

		return token;
	}

	private RefreshToken getRefreshTokenByString(String aTokenString) throws InvalidTokenException {

		TokenString tokenString = TokenString.valueOf(aTokenString);

		RefreshToken token = refreshTokenDao.findOne(tokenString.getTokenId());

		if (token == null) {

			log.debug("Refresh token not found [" + aTokenString + "].");

			throw new InvalidTokenException();
		}

		if (!passwordEncoder.matches(tokenString.getTokenSecret(), token.getSecret())) {

			log.debug("Refresh token secret does not match [" + aTokenString + "].");

			throw new InvalidTokenException();
		}

		return token;
	}

	private Date getTokenExpiration(BaseToken aToken) {
		return new Date(aToken.getDate().getTime() + accessTokenLifetime * 1000);
	}

	private class AuthenticationImpl implements Authentication {

		private String accessToken;

		private Date accessTokenExpiration;

		private String refreshToken;

		private Date refreshTokenExpiration;

		private User user;

		@Override
		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String aAccessToken) {
			accessToken = aAccessToken;
		}

		@Override
		public Date getAccessTokenExpiration() {
			return accessTokenExpiration;
		}

		public void setAccessTokenExpiration(Date aAccessTokenExpiration) {
			accessTokenExpiration = aAccessTokenExpiration;
		}

		@Override
		public String getRefreshToken() {
			return refreshToken;
		}

		public void setRefreshToken(String aRefreshToken) {
			refreshToken = aRefreshToken;
		}

		@Override
		public Date getRefreshTokenExpiration() {
			return refreshTokenExpiration;
		}

		public void setRefreshTokenExpiration(Date aRefreshTokenExpiration) {
			refreshTokenExpiration = aRefreshTokenExpiration;
		}

		@Override
		public User getUser() {
			return user;
		}

		public void setUser(User aUser) {
			user = aUser;
		}
	}

	private static class TokenString {

		private String tokenId;

		private String tokenSecret;

		public TokenString() {
			tokenId = UUID.randomUUID().toString().replaceAll("-", "");
			tokenSecret = RandomStringUtils.random(64, 33, 126, false, false, null, new SecureRandom());
		}

		public TokenString(String aTokenId, String aTokenSecret) {
			tokenId = aTokenId;
			tokenSecret = aTokenSecret;
		}

		public String getTokenId() {
			return tokenId;
		}

		public String getTokenSecret() {
			return tokenSecret;
		}

		public String toString() {
			return tokenId + tokenSecret;
		}

		public static TokenString valueOf(String aString) throws InvalidTokenException {

			if (aString == null || aString.length() < 32) {
				throw new InvalidTokenException();
			}

			return new TokenString(aString.substring(0, 32), aString.substring(32));
		}
	}

}
