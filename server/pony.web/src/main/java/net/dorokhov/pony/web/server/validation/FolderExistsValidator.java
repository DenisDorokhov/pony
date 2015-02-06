package net.dorokhov.pony.web.server.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;

public class FolderExistsValidator implements ConstraintValidator<FolderExists, String> {

	@Override
	public void initialize(FolderExists aAnnotation) {}

	@Override
	public boolean isValid(String aValue, ConstraintValidatorContext aContext) {
		return new File(aValue).isDirectory();
	}
}
