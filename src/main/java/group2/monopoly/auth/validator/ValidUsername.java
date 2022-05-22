package group2.monopoly.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// This is a custom annotation that we created to validate the username.
@Documented
@Constraint(validatedBy = UsernameConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "{group2.monopoly.auth.validator.ValidUsername.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
