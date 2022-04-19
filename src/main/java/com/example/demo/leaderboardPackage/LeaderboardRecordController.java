package com.example.demo.leaderboardPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/LeaderboardRecord")
public class LeaderboardRecordController {

    private final LeaderboardRecordService leaderboardRecordService;

    @Autowired
    public LeaderboardRecordController(LeaderboardRecordService LeaderboardRecordService) {
        this.leaderboardRecordService = LeaderboardRecordService;
    }

    @RequestMapping("/get")
    public List<LeaderboardRecord> getLeaderboardRecord() {
        return leaderboardRecordService.getLeaderboardRecords();
    }

    @PostMapping("/register")
    public void registerLeaderboardRecord(@RequestBody LeaderboardRecord LeaderboardRecord) {
        leaderboardRecordService.add(LeaderboardRecord);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLeaderboardRecord(@PathVariable("id") Long id) {
        leaderboardRecordService.delete(id);
    }


}
