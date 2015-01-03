package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.CredentialsDto;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.command.CreateUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateUserCommandDto;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;

import java.util.List;

public interface UserServiceFacade {

	public UserDto getById(Long aId) throws ObjectNotFoundException;

	public List<UserDto> getAll();

	public UserDto create(CreateUserCommandDto aCommand) throws UserExistsException;
	public UserDto update(UpdateUserCommandDto aCommand) throws UserNotFoundException, UserExistsException;

	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException;

	public String authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException;

	public void logout(String aToken) throws InvalidTokenException;

	public UserDto getAuthenticatedUser() throws NotAuthenticatedException;

	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommandDto aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException;

}
