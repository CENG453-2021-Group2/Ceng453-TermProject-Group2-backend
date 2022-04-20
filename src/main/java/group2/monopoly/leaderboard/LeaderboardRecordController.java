package group2.monopoly.leaderboard;

import group2.monopoly.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group2.monopoly.payload.GenericResponse;

import java.util.List;

/**
 * It's a controller that handles requests to the /LeaderboardRecord endpoint
 */
@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController {

    private final LeaderboardRecordRepository leaderboardRecordRepository;
    private final PlayerRepository playerRepository;

    // It's a constructor that takes in two repositories as parameters.
    @Autowired
    public LeaderboardRecordController(LeaderboardRecordRepository leaderboardRecordRepository, PlayerRepository playerRepository) {
        this.leaderboardRecordRepository = leaderboardRecordRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * This function returns a list of all the leaderboard records in the database.
     * 
     * @return A list of all the leaderboard records in the database.
     */
    @RequestMapping("/get")
    public List<LeaderboardRecord> getLeaderboardRecord() {
        return leaderboardRecordRepository.findAll();
    }

    /**
     * If the player exists, save the leaderboard record
     * 
     * @param leaderboardRecord The leaderboard record to be saved.
     * @return A ResponseEntity object is being returned.
     */
    @PostMapping("/save")
    public ResponseEntity<GenericResponse> registerLeaderboardRecord(@RequestBody LeaderboardRecord leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerRepository.findById(playerId).isEmpty()) {
            return ResponseEntity.badRequest().body( GenericResponse.error("Player not found"));
        }
        leaderboardRecordRepository.save(leaderboardRecord);
        return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord saved"));
    }

    /**
     * It deletes a leaderboard record by id.
     * 
     * @param id The id of the leaderboard record to be deleted
     * @return A ResponseEntity object is being returned.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse> deleteLeaderboardRecord(@PathVariable("id") Long id) {
        try {
            leaderboardRecordRepository.removeById(id);
            return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord deleted"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(GenericResponse.error("LeaderboardRecord not found"));
        }
    }

}
