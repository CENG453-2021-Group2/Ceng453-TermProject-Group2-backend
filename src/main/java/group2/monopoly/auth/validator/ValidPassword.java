package group2.monopoly.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * A custom annotation that is used to validate the password.
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
public @interface ValidPassword {
    String message() default "{group2.monopoly.auth.validator.ValidPassword.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
