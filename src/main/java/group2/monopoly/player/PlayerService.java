package group2.monopoly.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * It's a service class that provides methods for adding and deleting players
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    // It's a constructor that takes a PlayerRepository object as a parameter.
    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * If a player with the same email address already exists, throw an exception. Otherwise, save the
     * player
     * 
     * @param player The player object that is being added to the database.
     */
    public void add(Player player) {
        Optional<Player> playerbyEmail = playerRepository.findByEmail(player.getEmail());

        if (playerbyEmail.isPresent()) {
            throw new IllegalStateException("Player with email " + player.getEmail() + " already exists");
        }
        System.out.println("Adding player: " + player.getEmail());
        playerRepository.save(player);
    }

    /**
     * If the player exists, delete it. If the player doesn't exist, throw an exception
     * 
     * @param id The id of the player to delete
     */
    public void delete(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.deleteById(id);
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }
}
