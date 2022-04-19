package com.example.demo.leaderboardPackage;

import com.example.demo.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.player.PlayerService;

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
    public void registerLeaderboardRecord(@RequestBody LeaderboardRecord leaderboardRecord) {
        Long playerId = leaderboardRecord.getId();
        if (playerRepository.findPlayerbyId(playerId).isEmpty()) {
            throw new RuntimeException("Player with id " + playerId + " does not exist");
        }
        leaderboardRecordRepository.save(leaderboardRecord);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLeaderboardRecord(@PathVariable("id") Long id) {
        leaderboardRecordRepository.removeById(id);
    }


}
