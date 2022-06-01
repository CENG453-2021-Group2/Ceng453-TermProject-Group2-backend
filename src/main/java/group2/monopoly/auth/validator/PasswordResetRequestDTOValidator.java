package group2.monopoly.auth.validator;


import group2.monopoly.auth.payload.PasswordResetRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Class used to validate password reset requests.
 */
public class PasswordResetRequestDTOValidator implements ConstraintValidator<ValidPasswordResetRequestDTO, PasswordResetRequestDTO> {
    @Override
    public void initialize(ValidPasswordResetRequestDTO constraintAnnotation) {
    }

    /**
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return result of the validation
     */
    @Override
    public boolean isValid(PasswordResetRequestDTO value, ConstraintValidatorContext context) {
        if (value.getPassword() == null || value.getConfirmPassword() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be empty.").addConstraintViolation();
            return false;
        }

        if (!value.getPassword().equals(value.getConfirmPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords should match.").addConstraintViolation();
            return false;
        }

        return true;
    }
}
