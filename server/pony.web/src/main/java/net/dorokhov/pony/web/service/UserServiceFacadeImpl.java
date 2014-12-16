package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.domain.UserToken;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.CredentialsDto;
import net.dorokhov.pony.web.domain.RoleDto;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.UserTokenDto;
import net.dorokhov.pony.web.domain.command.CreateUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceFacadeImpl implements UserServiceFacade {

	private UserService userService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getById(Long aId) {
		return dtoConverter.userToDto(userService.getById(aId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAll() {

		List<UserDto> dto = new ArrayList<>();

		for (User user : userService.getAll()) {
			dto.add(dtoConverter.userToDto(user));
		}

		return dto;
	}

	@Override
	@Transactional
	public UserDto create(CreateUserCommand aCommand) throws UserExistsException {

		User user = new User();

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());
		user.setPassword(aCommand.getPassword());
		user.setRoles(dtoToRoles(aCommand.getRole()));

		return dtoConverter.userToDto(userService.create(user));
	}

	@Override
	@Transactional
	public UserDto update(UpdateUserCommand aCommand) throws UserNotFoundException, UserExistsException {

		User user = userService.getById(aCommand.getId());

		if (user == null) {
			throw new UserNotFoundException(aCommand.getId());
		}

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());
		user.setRoles(dtoToRoles(aCommand.getRole()));

		return dtoConverter.userToDto(userService.update(user, aCommand.getPassword()));
	}

	@Override
	@Transactional
	public UserTokenDto authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException {
		return dtoConverter.userTokenToDto(userService.authenticate(aCredentials.getEmail(), aCredentials.getPassword()));
	}

	@Override
	@Transactional
	public void logout(UserTokenDto aToken) throws InvalidTokenException {
		userService.logout(new UserToken(aToken.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getAuthenticatedUser() throws NotAuthenticatedException {
		return dtoConverter.userToDto(userService.getAuthenticatedUser());
	}

	@Override
	@Transactional
	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommand aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException {

		User user = userService.getById(userService.getAuthenticatedUser().getId());

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());

		return dtoConverter.userToDto(userService.updateAuthenticatedUser(user, aCommand.getOldPassword(), aCommand.getNewPassword()));
	}

	@Override
	public boolean validateEmail(CreateUserCommand aCommand) {
		return validateEmail(null, aCommand.getEmail());
	}

	@Override
	public boolean validateEmail(UpdateUserCommand aCommand) {
		return validateEmail(userService.getById(aCommand.getId()), aCommand.getEmail());
	}

	@Override
	public boolean validateEmail(UpdateCurrentUserCommand aCommand) {

		User user = null;

		try {
			user = userService.getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			// User is not authenticated
		}

		return validateEmail(user, aCommand.getEmail());
	}

	private boolean validateEmail(User aUser, String aEmail) {

		User existingUser = userService.getByEmail(aEmail);

		if (aUser != null) {
			return (existingUser == null || !existingUser.getId().equals(aUser.getId()));
		} else {
			return (existingUser == null);
		}
	}

	private Set<String> dtoToRoles(RoleDto aDto) {

		Set<String> roles = new HashSet<>();

		switch (aDto) {

			case USER:
				roles.add(RoleDto.Strings.USER);
				break;

			case ADMIN:
				roles.add(RoleDto.Strings.USER);
				roles.add(RoleDto.Strings.ADMIN);
				break;
		}

		return roles;
	}
}
