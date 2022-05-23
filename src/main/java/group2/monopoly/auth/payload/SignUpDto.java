package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidUsername;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * DTO used for creating new accounts.
 * <br>
 * Fields are subject to validations {@link ValidUsername}, {@link Email}, and
 * {@link ValidPassword} where applicable and can't be null. Furthermore, fields
 * {@link #password} and {@link #confirmPassword} should match.
 */
@Data
public class SignUpDto {
    @NotNull
    @ValidUsername
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    @ValidPassword
    private String confirmPassword;
}
