package group2.monopoly.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used for validating username strings.
 */
@Documented
@Constraint(validatedBy = UsernameConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "{group2.monopoly.auth.validator.ValidUsername.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
