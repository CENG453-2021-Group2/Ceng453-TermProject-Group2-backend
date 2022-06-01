package group2.monopoly.game.service;

import group2.monopoly.game.entity.Player;
import group2.monopoly.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class ScoreboardService {
    private final PlayerRepository playerRepository;

    @Autowired
    public ScoreboardService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> bestOfAllTimes() {
        Instant inst = Instant.now();
        return playerRepository.findAllByGameNotNull(Date.from(inst.minus(7, ChronoUnit.DAYS)),
                Date.from(inst));
    }

    public List<Player> bestOfLastWeek() {
        return playerRepository.findAllByGameNotNull(new Date(0), new Date());
    }
}
