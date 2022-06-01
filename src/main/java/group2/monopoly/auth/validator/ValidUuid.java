package group2.monopoly.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for validation of UUID strings.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
public @interface ValidUuid {
    String message() default "{invalid.uuid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
