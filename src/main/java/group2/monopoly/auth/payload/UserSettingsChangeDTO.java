package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidUsername;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * DTO for changing user account settings.
 * <br>
 * Settings that are not intended to be changed should be omitted.
 * The fields are validated with validators {@link ValidUsername}, {@link Email},
 * {@link ValidPassword} where applicable. If new password fields are supplied, they should match.
 */
@Data
public class UserSettingsChangeDTO {
    @Nullable
    @ValidUsername
    private String username;

    @Nullable
    @Email
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    @Nullable
    @ValidPassword
    private String newPassword;

    @Nullable
    @ValidPassword
    private String confirmNewPassword;
}