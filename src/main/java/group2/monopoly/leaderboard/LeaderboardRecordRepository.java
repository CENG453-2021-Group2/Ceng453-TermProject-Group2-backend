package group2.monopoly.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRecordRepository extends JpaRepository<LeaderboardRecord, Long> {
    List<LeaderboardRecord> findAll();
    boolean removeById(Long id);
}