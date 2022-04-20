package group2.monopoly.leaderboard7;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Creating a repository for the LeaderboardRecord7 class.
@Repository
public interface LeaderboardRecordRepository7 extends JpaRepository<LeaderboardRecord7, Long> {
    List<LeaderboardRecord7> findAll();
    boolean removeById(Long id);
}
