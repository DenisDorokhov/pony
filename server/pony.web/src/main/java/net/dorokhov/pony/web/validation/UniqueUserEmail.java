package net.dorokhov.pony.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserEmailValidator.class)
public @interface UniqueUserEmail {

	String message() default "{net.dorokhov.pony.web.validation.UniqueUserEmail.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
