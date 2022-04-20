package group2.monopoly.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void add(Player player) {
        Optional<Player> playerbyEmail = playerRepository.findByEmail(player.getEmail());

        if (playerbyEmail.isPresent()) {
            throw new IllegalStateException("Player with email " + player.getEmail() + " already exists");
        }
        System.out.println("Adding player: " + player.getEmail());
        playerRepository.save(player);
    }

    public void delete(Long id) {
        // TODO: authorization
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.deleteById(id);
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }
}
