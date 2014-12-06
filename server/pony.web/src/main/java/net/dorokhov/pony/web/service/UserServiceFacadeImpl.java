package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.domain.UserDto;
import net.dorokhov.pony.web.domain.UserTokenDto;
import net.dorokhov.pony.web.domain.command.SaveCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.SaveUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
	public UserDto getById(Long aId) {
		return dtoConverter.userToDto(userService.getById(aId));
	}

	@Override
	public List<UserDto> getAll() {

		List<UserDto> dto = new ArrayList<>();

		for (User user : userService.getAll()) {
			dto.add(dtoConverter.userToDto(user));
		}

		return dto;
	}

	@Override
	public UserDto create(SaveUserCommand aCommand) throws UserExistsException {

		User user = new User();

		user.setName(aCommand.getUser().getName());
		user.setEmail(aCommand.getUser().getEmail());
		user.setPassword(aCommand.getPassword());

		switch (aCommand.getUser().getRole()) {

			case USER:
				break;

			case ADMIN:
				break;
		}

		return null;
	}

	@Override
	public UserDto update(SaveUserCommand aCommand) throws UserNotFoundException, UserExistsException {
		return null;
	}

	@Override
	public UserTokenDto authenticate(String aEmail, String aPassword) throws InvalidCredentialsException {
		return dtoConverter.userTokenToDto(userService.authenticate(aEmail, aPassword));
	}

	@Override
	public void logout() throws InvalidTokenException {

	}

	@Override
	public UserDto getAuthenticatedUser() throws NotAuthenticatedException {
		return dtoConverter.userToDto(userService.getAuthenticatedUser());
	}

	@Override
	public UserDto updateAuthenticatedUser(SaveCurrentUserCommand aCommand) throws NotAuthenticatedException, NotAuthorizedException, InvalidCredentialsException, UserNotFoundException, UserExistsException {
		return null;
	}
}
