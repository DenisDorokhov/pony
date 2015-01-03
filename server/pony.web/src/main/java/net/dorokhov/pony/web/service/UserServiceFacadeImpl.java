package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.CredentialsDto;
import net.dorokhov.pony.web.domain.RoleDto;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.command.CreateUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateUserCommandDto;
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

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getById(Long aId) {
		return UserDto.valueOf(userService.getById(aId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAll() {

		List<UserDto> dto = new ArrayList<>();

		for (User user : userService.getAll()) {
			dto.add(UserDto.valueOf(user));
		}

		return dto;
	}

	@Override
	@Transactional
	public UserDto create(CreateUserCommandDto aCommand) throws UserExistsException {

		User user = new User();

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());
		user.setPassword(aCommand.getPassword());
		user.setRoles(dtoToRoles(aCommand.getRole()));

		return UserDto.valueOf(userService.create(user));
	}

	@Override
	@Transactional
	public UserDto update(UpdateUserCommandDto aCommand) throws UserNotFoundException, UserExistsException {

		User user = userService.getById(aCommand.getId());

		if (user == null) {
			throw new UserNotFoundException(aCommand.getId());
		}

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());
		user.setRoles(dtoToRoles(aCommand.getRole()));

		return UserDto.valueOf(userService.update(user, aCommand.getPassword()));
	}

	@Override
	@Transactional
	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException {
		userService.delete(aId);
	}

	@Override
	@Transactional
	public String authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException {
		return userService.authenticate(aCredentials.getEmail(), aCredentials.getPassword());
	}

	@Override
	@Transactional
	public void logout(String aToken) throws InvalidTokenException {
		userService.logout(aToken);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getAuthenticatedUser() throws NotAuthenticatedException {
		return UserDto.valueOf(userService.getAuthenticatedUser());
	}

	@Override
	@Transactional
	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommandDto aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException {

		User user = userService.getById(userService.getAuthenticatedUser().getId());

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());

		return UserDto.valueOf(userService.updateAuthenticatedUser(user, aCommand.getOldPassword(), aCommand.getNewPassword()));
	}

	private Set<String> dtoToRoles(RoleDto aDto) {

		Set<String> roles = new HashSet<>();

		switch (aDto) {

			case USER:
				roles.add(RoleDto.USER.toString());
				break;

			case ADMIN:
				roles.add(RoleDto.USER.toString());
				roles.add(RoleDto.ADMIN.toString());
				break;
		}

		return roles;
	}

}
