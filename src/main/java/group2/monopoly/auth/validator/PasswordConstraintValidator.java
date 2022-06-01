package group2.monopoly.auth.validator;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Class used to validate passwords.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword contactNumber) {
    }

    /**
     * If the password is null, return false and add a message to the constraint validator
     * context. If
     * the password is not null, validate it using the password validator and return true if it's
     * valid, otherwise return false and add the error messages to the constraint validator context
     *
     * @param passwordString             The password string to validate.
     * @param constraintValidatorContext This is the context of the constraint.
     * @return A boolean value.
     */
    @Override
    public boolean isValid(String passwordString,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (passwordString == null) return true;
        PasswordValidator validator = new PasswordValidator(
                new LengthRule(8, 64),
                new CharacterRule(EnglishCharacterData.Alphabetical, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));
        RuleResult result = validator.validate(new PasswordData(passwordString));
        if (result.isValid()) {
            return true;
        }
        constraintValidatorContext.disableDefaultConstraintViolation();
        List<String> messages = validator.getMessages(result);
        for (String message : messages) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return false;
    }
}
