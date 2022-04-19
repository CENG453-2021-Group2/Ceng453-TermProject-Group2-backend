package com.example.demo.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    public List<Player> getPlayers() {
        return playerRepository.findAll();

    }

    public void add(Player player) {
        Optional<Player> playerbyEmail = playerRepository.findPlayerByEmail(player.getEmail());

        if (playerbyEmail.isPresent()) {
            throw new IllegalStateException("Player with email " + player.getEmail() + " already exists");
        }

        playerRepository.save(player);
    }

    public void delete(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.deleteById(id);
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }

    public Player getPlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        }
        else {
            throw new IllegalStateException("Player with id " + id + " does not exist");
        }
    }

    public Long getPlayerId(String email) {
        Optional<Player> player = playerRepository.findPlayerByEmail(email);
        if (player.isPresent()) {
            return player.get().getId();
        }
        else {
            throw new IllegalStateException("Player with email " + email + " does not exist");
        }
    }


}
