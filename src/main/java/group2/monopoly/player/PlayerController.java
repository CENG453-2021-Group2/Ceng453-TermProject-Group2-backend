package group2.monopoly.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerRepository playerRepository, PlayerService playerService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    @RequestMapping("/get")
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @PostMapping("/register")
    public void registerPlayer(@RequestBody Player player) {
        playerService.add(player);
    }

    // Not all players should delete other players TODO: fix this
    @DeleteMapping("/delete/{id}")
    public void deletePlayer(@PathVariable("id") Long id) {
        playerRepository.removeById(id);
    }
}
