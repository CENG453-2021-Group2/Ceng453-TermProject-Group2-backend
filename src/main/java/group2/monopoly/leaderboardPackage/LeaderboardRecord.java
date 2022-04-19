package group2.monopoly.leaderboardPackage;

import javax.persistence.*;

@Entity
@Table
public class LeaderboardRecord {
    @Id
    @SequenceGenerator(
            name = "leaderboard_record_seq",
            sequenceName = "leaderboard_record_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "leaderboard_record_seq"
    )

    private Long id;
    private Long score;

    public LeaderboardRecord() {
    }

    public LeaderboardRecord(Long id, Long score) {
        setId(id); // using this function to validate id
        this.score = score;
    }

    private void setId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id must be non-negative");
        }
        this.id = id;
    }

    public Long getScore() {
        return score;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "LeaderboardRecord{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }


}
