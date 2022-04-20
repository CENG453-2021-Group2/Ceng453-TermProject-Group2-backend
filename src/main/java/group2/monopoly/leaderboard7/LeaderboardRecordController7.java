package group2.monopoly.leaderboard7;

import group2.monopoly.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group2.monopoly.payload.GenericResponse;

import java.util.List;

/**
 * It's a controller class that handles requests to the LeaderboardRecord endpoint
 */
@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController7 {

    private final LeaderboardRecordRepository7 leaderboardRecordRepository7;
    private final PlayerRepository playerRepository;

    // It's a constructor that takes in two parameters, a LeaderboardRecordRepository7 and a
    // PlayerRepository.
    @Autowired
    public LeaderboardRecordController7(LeaderboardRecordRepository7 leaderboardRecordRepository7, PlayerRepository playerRepository) {
        this.leaderboardRecordRepository7 = leaderboardRecordRepository7;
        this.playerRepository = playerRepository;
    }

    /**
     * This function is a GET request that returns a list of all the leaderboard records in the
     * database
     * 
     * @return A list of all the leaderboard records.
     */
    @RequestMapping("/get77")
    public List<LeaderboardRecord7> getLeaderboardRecord() {
        return leaderboardRecordRepository7.findAll();
    }

    /**
     * It takes a leaderboard record, checks if the player exists, and if so, saves the leaderboard
     * record
     * 
     * @param leaderboardRecord This is the object that will be sent to the server.
     * @return A response entity with a generic response.
     */
    @PostMapping("/save777")
    public ResponseEntity<GenericResponse> registerLeaderboardRecord(@RequestBody LeaderboardRecord7 leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerRepository.findById(playerId).isEmpty()) {
            return ResponseEntity.badRequest().body( GenericResponse.error("Player not found"));
        }
        leaderboardRecordRepository7.save(leaderboardRecord);
        return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord saved"));
    }

    /**
     * Delete a leaderboard record by id
     * 
     * @param id The id of the leaderboard record to delete.
     * @return A response entity with a generic response.
     */
    @DeleteMapping("/delete777/{id}")
    public ResponseEntity<GenericResponse> deleteLeaderboardRecord(@PathVariable("id") Long id) {
        try {
            leaderboardRecordRepository7.removeById(id);
            return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord deleted"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(GenericResponse.error("LeaderboardRecord not found"));
        }
    }

}
