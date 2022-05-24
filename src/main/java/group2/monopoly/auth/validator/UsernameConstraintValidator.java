package group2.monopoly.auth.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * This class is used to validate the username.
 * It checks if the username is valid.
 */
public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public void initialize(ValidUsername contactNumber) {
    }

    /**
     * This method checks if the username is valid.
     *
     * @param username the username to be validated
     * @param constraintValidatorContext  the context of the validation
     * @return true if the username is valid, false otherwise
     */
    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext constraintValidatorContext) {

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
