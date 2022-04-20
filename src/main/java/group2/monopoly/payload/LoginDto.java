package group2.monopoly.payload;

import lombok.Data;

/**
 * It's a data transfer object (DTO) that holds the username and password for a login request
 */
@Data
public class LoginDto {
    private String username;
    private String password;
}
