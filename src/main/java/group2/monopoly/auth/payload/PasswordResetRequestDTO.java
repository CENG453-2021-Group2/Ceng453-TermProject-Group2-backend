package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidPasswordResetRequestDTO;
import group2.monopoly.auth.validator.ValidUuid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Password reset request DTO. {@link #password} and {@link #confirmPassword} fields should match.
 */
@Data
@ValidPasswordResetRequestDTO
public class PasswordResetRequestDTO {
    @NotNull
    @ValidUuid
    private String token;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    @ValidPassword
    private String confirmPassword;
}
