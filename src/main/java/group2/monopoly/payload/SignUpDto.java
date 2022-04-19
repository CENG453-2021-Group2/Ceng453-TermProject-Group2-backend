package group2.monopoly.payload;

import group2.monopoly.constraint.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class SignUpDto {
    private String username;

    @Email
    private String email;

    @ValidPassword
    private String password;
}
