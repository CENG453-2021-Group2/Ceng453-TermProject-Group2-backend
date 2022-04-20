package group2.monopoly.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public void initialize(ValidUsername contactNumber) {
    }

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext cxt) {
        // 4-16 alphanumeric characters
        return username != null && username.matches("\\w{4,16}+");
    }

}
