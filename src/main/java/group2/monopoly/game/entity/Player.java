package group2.monopoly.game.entity;

import group2.monopoly.auth.entity.User;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "player")
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @SequenceGenerator(name = "player_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_seq")
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "money", nullable = false)
    private Integer money;

    @NonNull
    @Builder.Default
    @Column(name = "location", nullable = false)
    private Integer location = 0;

    @NonNull
    @Builder.Default
    @Column(name = "remaining_jail_time", nullable = false)
    private Integer remainingJailTime = 0;

    @NonNull
    @Builder.Default
    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @NonNull
    @Builder.Default
    @Column(name = "successive_doubles", nullable = false)
    private Integer successiveDoubles = 0;

    @NonNull
    @Column(name = "turn_order", nullable = false)
    private Integer turnOrder;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> ownedPurchasables = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Player player = (Player) o;
        return id != null && Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
