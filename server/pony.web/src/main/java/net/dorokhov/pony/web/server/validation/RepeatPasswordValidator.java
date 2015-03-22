package net.dorokhov.pony.web.server.validation;

import net.dorokhov.pony.web.shared.command.InstallCommandDto;
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
			return validateInstallCommand((InstallCommandDto) aObject, aContext);
		} else {
			log.warn("Object type [" + aObject.getClass() + "] is not supported.");
		}

		return false;
	}

	private boolean validateInstallCommand(InstallCommandDto aCommand, ConstraintValidatorContext aContext) {

		if (!Objects.equals(aCommand.getUserPassword(), aCommand.getUserRepeatPassword())) {

			addFieldError("userRepeatPassword", aContext);

			return false;
		}

		return true;
	}

	private void addFieldError(String aField, ConstraintValidatorContext aContext) {
		aContext.disableDefaultConstraintViolation();
		aContext.buildConstraintViolationWithTemplate(aContext.getDefaultConstraintMessageTemplate()).addNode(aField).addConstraintViolation();
	}

}
