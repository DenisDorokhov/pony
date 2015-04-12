package net.dorokhov.pony.web.server.validation;

import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.InstallCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class RepeatPasswordValidator implements ConstraintValidator<RepeatPassword, Object> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void initialize(RepeatPassword aAnnotation) {}

	@Override
	public boolean isValid(Object aObject, ConstraintValidatorContext aContext) {

		if (aObject instanceof InstallCommandDto) {
			return validateCommand((InstallCommandDto) aObject, aContext);
		} else if (aObject instanceof CreateUserCommandDto) {
			return validateCommand((CreateUserCommandDto) aObject, aContext);
		} else if (aObject instanceof UpdateUserCommandDto) {
			return validateCommand((UpdateUserCommandDto) aObject, aContext);
		} else if (aObject instanceof UpdateCurrentUserCommandDto) {
			return validateCommand((UpdateCurrentUserCommandDto) aObject, aContext);
		} else {
			log.warn("Object type [" + aObject.getClass() + "] is not supported.");
		}

		return false;
	}

	private boolean validateCommand(InstallCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!Objects.equals(aCommand.getUserPassword(), aCommand.getUserRepeatPassword())) {

			addFieldError("userRepeatPassword", aContext);

			return false;
		}

		return true;
	}

	private boolean validateCommand(CreateUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!Objects.equals(aCommand.getPassword(), aCommand.getRepeatPassword())) {

			addFieldError("repeatPassword", aContext);

			return false;
		}

		return true;
	}

	private boolean validateCommand(UpdateUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!Objects.equals(aCommand.getPassword(), aCommand.getRepeatPassword())) {

			addFieldError("repeatPassword", aContext);

			return false;
		}

		return true;
	}

	private boolean validateCommand(UpdateCurrentUserCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!Objects.equals(aCommand.getNewPassword(), aCommand.getRepeatNewPassword())) {

			addFieldError("repeatNewPassword", aContext);

			return false;
		}

		return true;
	}

	private void addFieldError(String aField, ConstraintValidatorContext aContext) {
		aContext.disableDefaultConstraintViolation();
		aContext.buildConstraintViolationWithTemplate(aContext.getDefaultConstraintMessageTemplate()).addNode(aField).addConstraintViolation();
	}

}
