package group2.monopoly.leaderboard7;

import group2.monopoly.leaderboard.LeaderboardRecord;
import group2.monopoly.player.Player;

import javax.persistence.*;

@Entity
@Table(name = "leaderboard7")
public class LeaderboardRecord7 {
    @Id
    @Column(name = "leaderboard_id")
    private Long id;

    /*
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "leaderboard_id", referencedColumnName = "id")
    private LeaderboardRecord leaderboardRecord;
     */

    public LeaderboardRecord7() {
    }

    public LeaderboardRecord7(Long id) {
        setId(id); // using this function to validate id
    }

    private void setId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id must be non-negative");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
