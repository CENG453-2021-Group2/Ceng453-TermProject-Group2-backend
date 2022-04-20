package group2.monopoly.leaderboard7;

import group2.monopoly.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import group2.monopoly.payload.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController7 {

    private final LeaderboardRecordRepository7 leaderboardRecordRepository7;
    private final PlayerRepository playerRepository;

    @Autowired
    public LeaderboardRecordController7(LeaderboardRecordRepository7 leaderboardRecordRepository7, PlayerRepository playerRepository) {
        this.leaderboardRecordRepository7 = leaderboardRecordRepository7;
        this.playerRepository = playerRepository;
    }

    @RequestMapping("/get77")
    public List<LeaderboardRecord7> getLeaderboardRecord() {
        return leaderboardRecordRepository7.findAll();
    }

    @PostMapping("/save777")
    public ResponseEntity<GenericResponse> registerLeaderboardRecord(@RequestBody LeaderboardRecord7 leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerRepository.findById(playerId).isEmpty()) {
            return ResponseEntity.badRequest().body( GenericResponse.error("Player not found"));
        }
        leaderboardRecordRepository7.save(leaderboardRecord);
        return ResponseEntity.ok(GenericResponse.message("LeaderboardRecord saved"));
    }

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
