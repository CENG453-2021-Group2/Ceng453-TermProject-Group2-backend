package group2.monopoly.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UsernameConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "{group2.monopoly.constraint.ValidUsername.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
