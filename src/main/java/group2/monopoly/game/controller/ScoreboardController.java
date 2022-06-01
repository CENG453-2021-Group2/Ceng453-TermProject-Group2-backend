package group2.monopoly.game.controller;

import group2.monopoly.game.entity.Player;
import group2.monopoly.game.service.ScoreboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/scoreboard", produces = "application/json", consumes = "application/json")
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    public ScoreboardController(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @GetMapping(value = "pastweek")
    List<Player> weeklyBest() {
        return scoreboardService.bestOfLastWeek();
    }

    @GetMapping(value = "alltime")
    List<Player> alltimeBest() {
        return scoreboardService.bestOfAllTimes();
    }

}
