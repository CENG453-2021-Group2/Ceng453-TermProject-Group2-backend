package group2.monopoly.auth.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Regex based UUID validator class.
 */
public class UuidValidator implements ConstraintValidator<ValidUuid, String> {
    @Override
    public void initialize(ValidUuid constraintAnnotation) {
    }

    /**
     * Validates the given UUID string with the following regex expression.
     * <br>
     * {@code ^[\da-fA-F]{8}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{4}-[\da-fA-F]{12}$}.
     * That is, hexadecimal digit groups of 8, 4, 4, 4, 12 separated with dash ('-') characters.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return the result of the validation
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches(
                "^[\\da-fA-F]{8}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F" +
                "]{12}$"
        );

    }
}
