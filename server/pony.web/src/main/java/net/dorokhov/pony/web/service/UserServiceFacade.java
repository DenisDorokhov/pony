package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.UserTokenDto;
import net.dorokhov.pony.web.domain.command.SaveCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.SaveUserCommand;

import java.util.List;

public interface UserServiceFacade {

	public UserDto getById(Long aId);

	public List<UserDto> getAll();

	public UserDto create(SaveUserCommand aCommand) throws UserExistsException;
	public UserDto update(SaveUserCommand aCommand) throws UserNotFoundException, UserExistsException;

	public UserTokenDto authenticate(String aEmail, String aPassword) throws InvalidCredentialsException;

	public void logout() throws InvalidTokenException;

	public UserDto getAuthenticatedUser() throws NotAuthenticatedException;

	public UserDto updateAuthenticatedUser(SaveCurrentUserCommand aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidCredentialsException, UserNotFoundException, UserExistsException;
}
