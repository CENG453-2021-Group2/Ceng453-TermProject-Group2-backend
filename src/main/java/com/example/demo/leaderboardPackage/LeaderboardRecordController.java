package com.example.demo.leaderboardPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.player.PlayerService;

import java.util.List;

@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController {

    private final LeaderboardRecordService leaderboardRecordService;
    private final PlayerService playerService;

    @Autowired
    public LeaderboardRecordController(LeaderboardRecordService LeaderboardRecordService, PlayerService playerService) {
        this.leaderboardRecordService = LeaderboardRecordService;
        this.playerService = playerService;
    }

    @RequestMapping("/get")
    public List<LeaderboardRecord> getLeaderboardRecord() {
        return leaderboardRecordService.getLeaderboardRecords();
    }

    @PostMapping("/save")
    public void registerLeaderboardRecord(@RequestBody LeaderboardRecord leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerService.getPlayer(playerId) == null) {
            throw new RuntimeException("Player with id " + playerId + " does not exist");
        }
        leaderboardRecordService.add(leaderboardRecord);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLeaderboardRecord(@PathVariable("id") Long id) {
        leaderboardRecordService.delete(id);
    }


}
