package group2.monopoly.auth.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Password reset token object representation.
 */
@Getter
@Setter
@ToString
@Entity(name = "user_password_reset_token")
@NoArgsConstructor
public class UserPasswordReset implements Serializable {
    /**
     * The user whose password can be reset with this token.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Actual token.
     * See <a href="https://stackoverflow.com/a/48463015">https://stackoverflow.com/a/48463015</a> for rationale behind length=16.
     */
    @Id
    @NonNull
    @Column(name = "token", nullable = false, length = 16)
    UUID token;

    public UserPasswordReset(User user, @NonNull UUID token, @NonNull Date validUntil) {
        this.user = user;
        this.token = token;
        this.validUntil = validUntil;
    }

    /**
     * The token can be used to reset password until this time.
     */
    @NonNull
    @Column(name="valid_until", nullable = false)
    private Date validUntil;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        UserPasswordReset that = (UserPasswordReset) o;
        return user != null && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
