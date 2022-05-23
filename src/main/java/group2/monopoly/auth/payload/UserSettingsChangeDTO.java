package group2.monopoly.auth.payload;

import group2.monopoly.auth.validator.ValidPassword;
import group2.monopoly.auth.validator.ValidUsername;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

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