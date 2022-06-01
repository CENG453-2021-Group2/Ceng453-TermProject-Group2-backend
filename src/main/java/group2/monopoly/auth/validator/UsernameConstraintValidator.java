package group2.monopoly.auth.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Class used to validate the username.
 */
public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public void initialize(ValidUsername contactNumber) {
    }

    /**
     * Checks if the username is valid.
     *
     * @param username the username to be validated
     * @param constraintValidatorContext  the context of the validation
     * @return true if the username is valid, false otherwise
     */
    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (username == null) {
            return true;
        }
        // 4-16 alphanumeric characters
        if (username.matches("\\w{4,16}+")) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                "username must be an alphanumeric string of length between 4 and 16"
        ).addConstraintViolation();
        return false;
    }

}
