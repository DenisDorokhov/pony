package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.AuthenticationDto;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.UserDto;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;

import java.util.List;

public interface UserServiceFacade {

	public UserDto getById(Long aId) throws ObjectNotFoundException;

	public List<UserDto> getAll();

	public UserDto create(CreateUserCommandDto aCommand) throws UserExistsException;
	public UserDto update(UpdateUserCommandDto aCommand) throws UserNotFoundException, UserExistsException;

	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException;

	public AuthenticationDto authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException;

	public UserDto logout(String aToken) throws InvalidTokenException;

	public UserDto getAuthenticatedUser() throws NotAuthenticatedException;

	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommandDto aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException;

}
