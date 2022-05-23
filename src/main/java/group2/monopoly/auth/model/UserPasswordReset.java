package group2.monopoly.auth.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Password reset token object representation.
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class UserPasswordReset {
    /**
     * The user whose password can be reset with this token.
     */
    @Id
    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    /**
     * Actual token.
     */
    @NonNull
    @Column(nullable = false)
    UUID token;

    /**
     * The token can be used to reset password until this time.
     */
    @NonNull
    @Column(nullable = false)
    private Date validUntil;

}
