package group2.monopoly.auth.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UuidValidator implements ConstraintValidator<ValidUuid, UUID> {
    @Override
    public void initialize(ValidUuid constraintAnnotation) {
    }

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        return value.toString().matches(
                "^[\\da-fA-F]{8}\b-[\\da-fA-F]{4}\b-[\\da-fA-F]{4}\b-[\\da-fA-F]{4}\b-[\\da-fA-F]{12}$"
        );

    }
}
