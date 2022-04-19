package group2.monopoly.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
public @interface ValidPassword {
    String message() default "Invalid Password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
