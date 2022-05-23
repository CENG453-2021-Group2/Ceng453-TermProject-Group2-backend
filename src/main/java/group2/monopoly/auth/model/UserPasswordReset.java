package group2.monopoly.auth.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class UserPasswordReset {
    @Id
    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @NonNull
    @Column(nullable = false)
    UUID token;

    @NonNull
    @Column(nullable = false)
    private Date validUntil;

}
