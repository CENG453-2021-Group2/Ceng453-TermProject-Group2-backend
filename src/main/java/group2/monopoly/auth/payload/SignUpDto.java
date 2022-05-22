package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidUsername;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * It's a DTO that contains a username, email, and password
 */
@Data
public class SignUpDto {
    @ValidUsername
    private String username;

    @Email
    private String email;

    @ValidPassword
    private String password;
}
