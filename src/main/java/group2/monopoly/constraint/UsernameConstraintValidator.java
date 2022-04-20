package group2.monopoly.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {
    /*
    * This class is used to validate the username.
    * It checks if the username is valid.
    * */

    @Override
    public void initialize(ValidUsername contactNumber) {
    }

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext constraintValidatorContext) {
        /*
        * This method checks if the username is valid.
        * @param username the username to be validated
        * @param context the context of the validation
        * @return true if the username is valid, false otherwise
         */
        // 4-16 alphanumeric characters
        if (username != null && username.matches("\\w{4,16}+")) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                "Username must be an alphanumeric string of length between 4 and 16."
        ).addConstraintViolation();
        return false;
    }

}
