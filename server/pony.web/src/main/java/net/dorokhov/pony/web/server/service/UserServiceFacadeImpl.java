package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.*;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public UserDto getById(Long aId) throws ObjectNotFoundException {

		User user = userService.getById(aId);

		if (user != null) {
			return dtoConverter.userToDto(user);
		}

		throw new ObjectNotFoundException(aId, ErrorCodes.USER_NOT_FOUND, "User [" + aId + "] not found.");
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAll() {
		return dtoConverter.listToDto(userService.getAll(), new DtoConverter.ListConverter<User, UserDto>() {
			@Override
			public UserDto convert(User aItem) {
				return dtoConverter.userToDto(aItem);
			}
		});
	}

	@Override
	@Transactional
	public UserDto create(CreateUserCommandDto aCommand) throws UserExistsException {

		User user = new User();

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());
		user.setPassword(aCommand.getPassword());
		user.setRoles(dtoToRoles(aCommand.getRole()));

		return dtoConverter.userToDto(userService.create(user));
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

		return dtoConverter.userToDto(userService.update(user, aCommand.getPassword()));
	}

	@Override
	@Transactional
	public void delete(Long aId) throws UserNotFoundException, UserSelfDeletionException {
		userService.delete(aId);
	}

	@Override
	@Transactional
	public AuthenticationDto authenticate(CredentialsDto aCredentials) throws InvalidCredentialsException {
		return authenticationToDto(userService.authenticate(aCredentials.getEmail(), aCredentials.getPassword()));
	}

	@Override
	public AuthenticationDto refreshToken(String aRefreshToken) throws InvalidTokenException {
		return authenticationToDto(userService.refreshToken(aRefreshToken));
	}

	@Override
	@Transactional
	public UserDto logout(String aToken) throws InvalidTokenException {
		return dtoConverter.userToDto(userService.logout(aToken));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getAuthenticatedUser() throws NotAuthenticatedException {
		return dtoConverter.userToDto(userService.getAuthenticatedUser());
	}

	@Override
	@Transactional
	public UserDto updateAuthenticatedUser(UpdateCurrentUserCommandDto aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException {

		User user = userService.getById(userService.getAuthenticatedUser().getId());

		user.setName(aCommand.getName());
		user.setEmail(aCommand.getEmail());

		return dtoConverter.userToDto(userService.updateAuthenticatedUser(user, aCommand.getOldPassword(), aCommand.getNewPassword()));
	}

	private AuthenticationDto authenticationToDto(UserService.Authentication aAuthentication) {

		AuthenticationDto dto = new AuthenticationDto();

		dto.setAccessToken(aAuthentication.getAccessToken());
		dto.setAccessTokenExpiration(aAuthentication.getAccessTokenExpiration());

		dto.setRefreshToken(aAuthentication.getRefreshToken());
		dto.setRefreshTokenExpiration(aAuthentication.getRefreshTokenExpiration());

		dto.setUser(dtoConverter.userToDto(aAuthentication.getUser()));

		return dto;
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
