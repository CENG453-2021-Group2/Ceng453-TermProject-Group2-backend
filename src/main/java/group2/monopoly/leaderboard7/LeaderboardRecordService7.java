package group2.monopoly.leaderboard7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardRecordService7 {

    private final LeaderboardRecordRepository7 leaderboardRecordRepository7;

    @Autowired
    public LeaderboardRecordService7(LeaderboardRecordRepository7 leaderboardRecordRepository7) {
        this.leaderboardRecordRepository7 = leaderboardRecordRepository7;
    }
    public List<LeaderboardRecord7> getLeaderboardRecords() {
        return leaderboardRecordRepository7.findAll();

    }


    public void add(LeaderboardRecord7 leaderboardRecord) {
        Optional<LeaderboardRecord7> leaderboardRecordbyId = leaderboardRecordRepository7.findById(leaderboardRecord.getId());

        if (leaderboardRecordbyId.isPresent()) {
            throw new IllegalStateException("Player with email " + leaderboardRecord.getId() + " already exists");
        }

        leaderboardRecordRepository7.save(leaderboardRecord);
    }

    public void delete(Long id) {
        Optional<LeaderboardRecord7> leaderboardRecord = leaderboardRecordRepository7.findById(id);
        if (leaderboardRecord.isPresent()) {
            leaderboardRecordRepository7.deleteById(id);
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }


}
