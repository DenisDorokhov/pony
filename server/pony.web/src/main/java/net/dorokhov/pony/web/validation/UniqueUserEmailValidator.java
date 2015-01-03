package net.dorokhov.pony.web.validation;

import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.user.UserService;
import net.dorokhov.pony.core.user.exception.NotAuthenticatedException;
import net.dorokhov.pony.web.domain.command.CreateUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.domain.command.UpdateUserCommandDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, Object> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserService userService;

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Override
	public void initialize(UniqueUserEmail aAnnotation) {}

	@Override
	public boolean isValid(Object aObject, ConstraintValidatorContext aContext) {

		if (aObject instanceof CreateUserCommandDto) {
			return validateCreateUserCommand((CreateUserCommandDto)aObject, aContext);
		} else if (aObject instanceof UpdateUserCommandDto) {
			return validateUpdateUserCommand((UpdateUserCommandDto)aObject, aContext);
		} else if (aObject instanceof UpdateCurrentUserCommandDto) {
			return validateUpdateCurrentUserCommand((UpdateCurrentUserCommandDto)aObject, aContext);
		} else {
			log.warn("Object type [" + aObject.getClass() + "] is not supported.");
		}

		return false;
	}

	private boolean validateCreateUserCommand(CreateUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!isEmailUnique(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private boolean validateUpdateUserCommand(UpdateUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!isEmailUnique(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private boolean validateUpdateCurrentUserCommand(UpdateCurrentUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!isEmailUnique(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private boolean isEmailUnique(CreateUserCommandDto aCommand) {
		return validateEmail(null, aCommand.getEmail());
	}

	private boolean isEmailUnique(UpdateUserCommandDto aCommand) {
		return validateEmail(userService.getById(aCommand.getId()), aCommand.getEmail());
	}

	private boolean isEmailUnique(UpdateCurrentUserCommandDto aCommand) {

		User user = null;

		try {
			user = userService.getAuthenticatedUser();
		} catch (NotAuthenticatedException e) {
			// User is not authenticated
		}

		return validateEmail(user, aCommand.getEmail());
	}

	private boolean validateEmail(User aUser, String aEmail) {

		User existingUser = userService.getByEmail(aEmail != null ? aEmail.trim() : null);

		if (aUser != null) {
			return (existingUser == null || !existingUser.getId().equals(aUser.getId()));
		} else {
			return (existingUser == null);
		}
	}

	private void addFieldError(String aField, ConstraintValidatorContext aContext) {
		aContext.disableDefaultConstraintViolation();
		aContext.buildConstraintViolationWithTemplate(aContext.getDefaultConstraintMessageTemplate()).addPropertyNode(aField).addConstraintViolation();
	}

}
