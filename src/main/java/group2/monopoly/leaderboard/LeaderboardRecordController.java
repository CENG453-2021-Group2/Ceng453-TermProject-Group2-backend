package group2.monopoly.leaderboard;

import group2.monopoly.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group2.monopoly.payload.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController {

    private final LeaderboardRecordRepository leaderboardRecordRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public LeaderboardRecordController(LeaderboardRecordRepository leaderboardRecordRepository, PlayerRepository playerRepository) {
        this.leaderboardRecordRepository = leaderboardRecordRepository;
        this.playerRepository = playerRepository;
    }

    @RequestMapping("/get")
    public List<LeaderboardRecord> getLeaderboardRecord() {
        return leaderboardRecordRepository.findAll();
    }

    @PostMapping("/save")
    public ResponseEntity<GenericResponse> registerLeaderboardRecord(@RequestBody LeaderboardRecord leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerRepository.findById(playerId).isEmpty()) {
            return ResponseEntity.badRequest().body( GenericResponse.error("Player not found"));
        }
        leaderboardRecordRepository.save(leaderboardRecord);
        return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord saved"));
    }

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
