package net.dorokhov.pony.web.validation;

import net.dorokhov.pony.web.domain.command.CreateUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateUserCommand;
import net.dorokhov.pony.web.service.UserServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, Object> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserServiceFacade userServiceFacade;

	@Autowired
	public void setUserServiceFacade(UserServiceFacade aUserServiceFacade) {
		userServiceFacade = aUserServiceFacade;
	}

	@Override
	public void initialize(UniqueUserEmail aAnnotation) {}

	@Override
	public boolean isValid(Object aObject, ConstraintValidatorContext aContext) {

		if (aObject instanceof CreateUserCommand) {
			return validateCreateUserCommand((CreateUserCommand)aObject, aContext);
		} else if (aObject instanceof UpdateUserCommand) {
			return validateUpdateUserCommand((UpdateUserCommand)aObject, aContext);
		} else if (aObject instanceof UpdateCurrentUserCommand) {
			return validateUpdateCurrentUserCommand((UpdateCurrentUserCommand)aObject, aContext);
		} else {
			log.warn("Object type [" + aObject.getClass() + "] is not supported.");
		}

		return false;
	}

	private boolean validateCreateUserCommand(CreateUserCommand aCommand, ConstraintValidatorContext aContext) {

		if (!userServiceFacade.validateEmail(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private boolean validateUpdateUserCommand(UpdateUserCommand aCommand, ConstraintValidatorContext aContext) {

		if (!userServiceFacade.validateEmail(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private boolean validateUpdateCurrentUserCommand(UpdateCurrentUserCommand aCommand, ConstraintValidatorContext aContext) {

		if (!userServiceFacade.validateEmail(aCommand)) {

			addFieldError("email", aContext);

			return false;
		}

		return true;
	}

	private void addFieldError(String aField, ConstraintValidatorContext aContext) {
		aContext.disableDefaultConstraintViolation();
		aContext.buildConstraintViolationWithTemplate(aContext.getDefaultConstraintMessageTemplate()).addPropertyNode(aField).addConstraintViolation();
	}
}
