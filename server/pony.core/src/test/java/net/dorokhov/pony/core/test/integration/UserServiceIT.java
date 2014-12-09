package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.dao.UserTicketDao;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserToken;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserServiceIT extends AbstractIntegrationCase {

	private UserService userService;

	private UserTicketDao userTicketDao;

	@Before
	public void setUp() throws Exception {
		userService = context.getBean(UserService.class);
		userTicketDao = context.getBean(UserTicketDao.class);
	}

	@Test
	public void testCrud() throws Exception {

		boolean isExceptionThrown;

		User user = userService.create(buildUser(1));

		checkUser(user, 1);
		checkUser(userService.getById(user.getId()), 1);

		isExceptionThrown = false;

		try {
			userService.create(user);
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		user.setId(null);

		isExceptionThrown = false;

		try {
			userService.create(user);
		} catch (UserExistsException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		user = buildUser(2);

		userService.create(user);

		Assert.assertEquals(2, userService.getAll().size());

		user.setName("TestNew");

		String oldPassword = user.getPassword();

		user = userService.update(user, null);

		Assert.assertEquals("TestNew", user.getName());
		Assert.assertEquals(oldPassword, user.getPassword());

		user = userService.update(user, "newPassword");

		Assert.assertNotEquals(oldPassword, user.getPassword());

		user.setEmail("test1@test.com");

		isExceptionThrown = false;

		try {
			userService.update(user, "newPassword");
		} catch (UserExistsException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		user = buildUser(3);
		user.setId(3L);

		isExceptionThrown = false;

		try {
			userService.update(user, "newPassword");
		} catch (UserNotFoundException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		user.setId(null);

		isExceptionThrown = false;

		try {
			userService.update(user, "newPassword");
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testAuthentication() throws Exception {

		boolean isExceptionThrown;

		userService.create(buildUser(1));

		UserToken token = userService.authenticate("test1@test.com", "password1");

		userService.authenticate(token);

		User user = userService.getAuthenticatedUser();

		checkUser(user, 1);

		user.setName("TestNew");

		user = userService.updateAuthenticatedUser(user, "password1", null);

		Assert.assertEquals("TestNew", user.getName());

		user = userService.updateAuthenticatedUser(user, "password1", "password2");
		user = userService.updateAuthenticatedUser(user, "password2", null);

		isExceptionThrown = false;

		try {
			userService.updateAuthenticatedUser(user, "password1", null);
		} catch (InvalidPasswordException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		User newUser = userService.create(buildUser(3));

		isExceptionThrown = false;

		try {
			userService.updateAuthenticatedUser(newUser, "password2", null);
		} catch (NotAuthorizedException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		userService.logout(token);

		isExceptionThrown = false;

		try {
			userService.updateAuthenticatedUser(user, "password2", null);
		} catch (NotAuthenticatedException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			userService.getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testFailedAuthentication() throws Exception {

		boolean isExceptionThrown;

		isExceptionThrown = false;

		try {
			userService.getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			userService.authenticate("test1@test.com", "password1");
		} catch (InvalidCredentialsException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			userService.authenticate(new UserToken("invalidToken"));
		} catch (InvalidTokenException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			userService.logout(new UserToken("invalidToken"));
		} catch (InvalidTokenException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		userService.create(buildUser(1));

		UserToken token = userService.authenticate("test1@test.com", "password1");

		userService.authenticate(token);
		userService.authenticate(token);

		Thread.sleep(6000);

		isExceptionThrown = false;

		try {
			userService.authenticate(token);
		} catch (InvalidTokenException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testCleaning() throws Exception {

		boolean isExceptionThrown;

		userService.create(buildUser(1));

		UserToken token1 = userService.authenticate("test1@test.com", "password1");
		UserToken token2 = userService.authenticate("test1@test.com", "password1");

		Thread.sleep(6000);

		userService.cleanTickets();

		Assert.assertEquals(0L, userTicketDao.count());

		isExceptionThrown = false;

		try {
			userService.authenticate(token1);
		} catch (InvalidTokenException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			userService.authenticate(token2);
		} catch (InvalidTokenException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	private void checkUser(User aUser, int aIndex) {

		Assert.assertNotNull(aUser.getId());
		Assert.assertNotNull(aUser.getCreationDate());

		Assert.assertNull(aUser.getUpdateDate());

		Assert.assertEquals("Test" + aIndex, aUser.getName());
		Assert.assertEquals("test" + aIndex + "@test.com", aUser.getEmail());
		Assert.assertNotNull(aUser.getPassword());
	}

	private User buildUser(int aIndex) {

		User user = new User();

		user.setName("Test" + aIndex);
		user.setEmail("test" + aIndex + "@test.com");
		user.setPassword("password" + aIndex);

		for (int i = 1; i <= aIndex; i++) {
			user.getRoles().add("role" + i);
		}

		return user;
	}
}
