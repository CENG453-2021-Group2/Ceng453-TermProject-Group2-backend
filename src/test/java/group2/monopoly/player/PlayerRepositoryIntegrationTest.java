package group2.monopoly.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    public void clearDatabase() {
        playerRepository.deleteAll();
    }

    @Test
    public void testFindByUsername_Exists() {
        Player player = getPlayer();
        playerRepository.save(player);
        Optional<Player> userFetched = playerRepository.findByUsername("example");
        assertTrue(userFetched.isPresent());
        userFetched.get().setId(1L);
        assertEquals(player, userFetched.get());
    }

    @Test
    public void testFindByUsername_DoesNotExist() {
        playerRepository.save(getPlayer());
        Optional<Player> userFetched = playerRepository.findByUsername("bogus");
        assertFalse(userFetched.isPresent());
    }

    @Test
    public void testExistsByUsername_Exists() {
        playerRepository.save(getPlayer());
        assertTrue(playerRepository.existsByUsername("example"));
    }

    @Test
    public void testExistsByUsername_DoesNotExist() {
        playerRepository.save(getPlayer());
        assertFalse(playerRepository.existsByUsername("tofu"));
    }

    @Test
    public void testFindByEmail_Exists() {
        Player player = getPlayer();
        playerRepository.save(player);
        Optional<Player> userFetched = playerRepository.findByEmail("example@example.com");
        assertTrue(userFetched.isPresent());
        userFetched.get().setId(1L);
        assertEquals(player, userFetched.get());
    }

    @Test
    public void testFindByEmail_DoesNotExist() {
        playerRepository.save(getPlayer());
        Optional<Player> userFetched = playerRepository.findByEmail("bogus@bogus.com");
        assertFalse(userFetched.isPresent());
    }

    @Test
    public void testExistsByEmail_Exists() {
        playerRepository.save(getPlayer());
        assertTrue(playerRepository.existsByEmail("example@example.com"));
    }

    @Test
    public void testExistsByEmail_DoesNotExist() {
        playerRepository.save(getPlayer());
        assertFalse(playerRepository.existsByEmail("bogus@example.com"));
    }

    @Test
    public void testSave_NoCollision() {
        playerRepository.save(getPlayer());
    }

    @Test
    public void testSave_CollisionUsername() {
        playerRepository.save(getPlayer());
        Player user2 = new Player();
        user2.setUsername("example");
        user2.setEmail("example2@example.com");
        user2.setPassword("password");
        // Flush the changes from cache to the DB to force the integrity violation
        assertThrows(DataIntegrityViolationException.class, () -> playerRepository.saveAndFlush(user2));
    }

    @Test
    public void testSave_CollisionEmail() {
        playerRepository.save(getPlayer());
        Player user2 = new Player();
        user2.setUsername("example2");
        user2.setEmail("example@example.com");
        user2.setPassword("password");
        playerRepository.save(user2);
        assertThrows(DataIntegrityViolationException.class, () -> playerRepository.saveAndFlush(user2));
    }

    private Player getPlayer() {
        Player player = new Player();
        player.setUsername("example");
        player.setEmail("example@example.com");
        // We only use plain passwords for test purposes.
        player.setPassword("password");
        return player;
    }

}
