package group2.monopoly.payload;

import group2.monopoly.constraint.ValidPassword;
import group2.monopoly.constraint.ValidUsername;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class SignUpDto {
    @ValidUsername
    private String username;

    @Email
    private String email;

    @ValidPassword
    private String password;
}
