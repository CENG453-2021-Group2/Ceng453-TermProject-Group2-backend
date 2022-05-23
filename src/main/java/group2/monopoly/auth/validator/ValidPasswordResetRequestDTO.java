package group2.monopoly.auth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordResetRequestDTOValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPasswordResetRequestDTO {
    String message() default "{invalid.tokenDTO}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
