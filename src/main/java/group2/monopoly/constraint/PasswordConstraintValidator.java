package group2.monopoly.constraint;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword contactNumber) {
    }

    @Override
    public boolean isValid(String passwordString,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (passwordString == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Password cannot be empty.")
                    .addConstraintViolation();
            return false;
        }
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
