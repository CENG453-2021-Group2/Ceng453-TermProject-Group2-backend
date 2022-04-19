package group2.monopoly.leaderboardPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardRecordService {

    private final LeaderboardRecordRepository leaderboardRecordRepository;

    @Autowired
    public LeaderboardRecordService(LeaderboardRecordRepository leaderboardRecordRepository) {
        this.leaderboardRecordRepository = leaderboardRecordRepository;
    }
    public List<LeaderboardRecord> getLeaderboardRecords() {
        return leaderboardRecordRepository.findAll();

    }


    public void add(LeaderboardRecord leaderboardRecord) {
        Optional<LeaderboardRecord> leaderboardRecordbyId = leaderboardRecordRepository.findById(leaderboardRecord.getId());

        if (leaderboardRecordbyId.isPresent()) {
            throw new IllegalStateException("Player with email " + leaderboardRecord.getId() + " already exists");
        }

        leaderboardRecordRepository.save(leaderboardRecord);
    }

    public void delete(Long id) {
        Optional<LeaderboardRecord> leaderboardRecord = leaderboardRecordRepository.findById(id);
        if (leaderboardRecord.isPresent()) {
            leaderboardRecordRepository.deleteById(id);
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }


}
