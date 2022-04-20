package group2.monopoly.player;

import lombok.*;


import javax.management.relation.Role;
import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "player", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class Player {
    @Id
    @SequenceGenerator(
            name = "player_seq",
            sequenceName = "player_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "player_seq"
    )
    private Long id;

    @Column(length = 255, nullable = false)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;
    @Column(length = 255, nullable = false)
    private String email;

    public Player() {
    }

    public Player(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Putting this just to make security work for now
    public Set<Role> getRoles() {
        return null;
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }
     */
}
