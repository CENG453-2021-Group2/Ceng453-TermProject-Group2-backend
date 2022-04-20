package group2.monopoly.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * It's a service class that uses the repository class to get data from the database
 */
@Service
public class LeaderboardRecordService {

    private final LeaderboardRecordRepository leaderboardRecordRepository;

    // It's a constructor that takes in a leaderboardRecordRepository and sets it to the
    // leaderboardRecordRepository variable.
    @Autowired
    public LeaderboardRecordService(LeaderboardRecordRepository leaderboardRecordRepository) {
        this.leaderboardRecordRepository = leaderboardRecordRepository;
    }

    /**
     * Get all the leaderboard records from the database and return them.
     * 
     * @return A list of all the leaderboard records.
     */
    public List<LeaderboardRecord> getLeaderboardRecords() {
        return leaderboardRecordRepository.findAll();
    }


    /**
     * If the leaderboard record with the given id already exists, throw an exception. Otherwise, save
     * the leaderboard record
     * 
     * @param leaderboardRecord The leaderboard record to be added to the database.
     */
    public void add(LeaderboardRecord leaderboardRecord) {
        Optional<LeaderboardRecord> leaderboardRecordbyId = leaderboardRecordRepository.findById(leaderboardRecord.getId());

        if (leaderboardRecordbyId.isPresent()) {
            throw new IllegalStateException("Player with email " + leaderboardRecord.getId() + " already exists");
        }

        leaderboardRecordRepository.save(leaderboardRecord);
    }

    /**
     * If the player exists, delete it. If the player doesn't exist, throw an exception
     * 
     * @param id The id of the leaderboard record to delete
     */
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
