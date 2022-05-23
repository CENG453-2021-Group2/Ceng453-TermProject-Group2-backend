package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidPasswordResetRequestDTO;
import group2.monopoly.auth.validator.ValidUuid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@ValidPasswordResetRequestDTO
public class PasswordResetRequestDTO {
    @NotNull
    @ValidUuid
    private UUID token;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    @ValidPassword
    private String confirmPassword;
}
