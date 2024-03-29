package group2.monopoly.auth.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO that holds the username and password for a login request.
 */
@Data
public class LoginDto {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
