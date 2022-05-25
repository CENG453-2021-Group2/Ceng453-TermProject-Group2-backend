package group2.monopoly.game.entity;

import group2.monopoly.auth.entity.User;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "game")
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @SequenceGenerator(name = "game_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany(mappedBy = "game")
    private List<Player> players;

    @NonNull
    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NonNull
    @Builder.Default
    @Column(name = "turn", nullable = false)
    private Long turn = 0L;

    @Column(name = "completion_date")
    private Date completionDate;

    @Embedded
    private GameTableConfiguration gameTableConfiguration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Game game = (Game) o;
        return id != null && Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}