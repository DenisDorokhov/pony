package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.CredentialsDto;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.command.CreateUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateUserCommand;

import java.util.List;

public interface UserServiceFacade {

	public UserDto getById(Long aId);

	public List<UserDto> getAll();

	public UserDto create(CreateUserCommand aCommand) throws UserExistsException;
	public UserDto update(UpdateUserCommand aCommand) throws UserNotFoundException, UserExistsException;

	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException;

	public String authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException;

	public void logout(String aToken) throws InvalidTokenException;

	public UserDto getAuthenticatedUser() throws NotAuthenticatedException;

	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommand aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException;

	public boolean validateEmail(CreateUserCommand aCommand);
	public boolean validateEmail(UpdateUserCommand aCommand);
	public boolean validateEmail(UpdateCurrentUserCommand aCommand);
}
