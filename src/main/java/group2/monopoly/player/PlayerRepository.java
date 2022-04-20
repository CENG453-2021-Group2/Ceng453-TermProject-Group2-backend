package group2.monopoly.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// A repository for the Player class. It extends JpaRepository, which is a Spring Data JPA class. It
// has methods for finding a player by email, username, or id, and for checking if a player exists by
// username or email. It also has a method for removing a player by id.
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByEmail(String email);
    Optional<Player> findByUsername(String username);
    Optional<Player> findById(Long id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    Boolean removeById(Long id);
}
