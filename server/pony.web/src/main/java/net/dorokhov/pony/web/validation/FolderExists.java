package net.dorokhov.pony.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FolderExistsValidator.class)
@ReportAsSingleViolation
@NotNull
public @interface FolderExists {

	String message() default "{net.dorokhov.pony.web.validation.FolderExists.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
