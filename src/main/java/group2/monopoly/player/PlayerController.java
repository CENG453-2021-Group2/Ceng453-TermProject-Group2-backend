package group2.monopoly.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import group2.monopoly.payload.GenericResponse;
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
    public ResponseEntity<GenericResponse> registerPlayer(@RequestBody Player player) {
        System.out.println("Registering player: " + player.getUsername());
        try {
            playerService.add(player);
            return new ResponseEntity<>(GenericResponse.message("Player registered successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(GenericResponse.message("Error registering player"), HttpStatus.BAD_REQUEST);
        }

    }

    // Not all players should delete other players TODO: fix this
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse> deletePlayer(@PathVariable("id") Long id) {
        try {
            playerRepository.removeById(id);
            return new ResponseEntity<>(GenericResponse.message("Player deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(GenericResponse.message("Error deleting player"), HttpStatus.BAD_REQUEST);
        }
    }
}
