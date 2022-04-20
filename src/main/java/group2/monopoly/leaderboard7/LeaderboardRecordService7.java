package group2.monopoly.leaderboard7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * It's a service class that uses the repository class to get data from the database
 */
@Service
public class LeaderboardRecordService7 {

    private final LeaderboardRecordRepository7 leaderboardRecordRepository7;

// It's a constructor that takes in a leaderboardRecordRepository7 object and sets it to the
// leaderboardRecordRepository7 variable.
    @Autowired
    public LeaderboardRecordService7(LeaderboardRecordRepository7 leaderboardRecordRepository7) {
        this.leaderboardRecordRepository7 = leaderboardRecordRepository7;
    }

    /**
    * It returns a list of all the leaderboard records.
    * 
    * @return A list of all the leaderboard records.
    */
    public List<LeaderboardRecord7> getLeaderboardRecords() {
        return leaderboardRecordRepository7.findAll();
    }

    /**
    * If the leaderboard record with the given ID already exists, throw an exception. Otherwise, save the
    * leaderboard record
    * 
    * @param leaderboardRecord The LeaderboardRecord7 object that you want to add to the database.
    */

    public void add(LeaderboardRecord7 leaderboardRecord) {
        Optional<LeaderboardRecord7> leaderboardRecordbyId = leaderboardRecordRepository7.findById(leaderboardRecord.getId());

        if (leaderboardRecordbyId.isPresent()) {
            throw new IllegalStateException("Player with email " + leaderboardRecord.getId() + " already exists");
        }

        leaderboardRecordRepository7.save(leaderboardRecord);
    }

    /**
     * If the player exists, delete the player
     * 
     * @param id The id of the player to be deleted.
     */
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
