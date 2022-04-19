package group2.monopoly.repository;

import group2.monopoly.entity.User;
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
// TODO: move this to PlayerRepositoryTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByUsername_Exists() {
        User user = getUser();
        userRepository.save(user);
        Optional<User> userFetched = userRepository.findByUsername("example");
        assertTrue(userFetched.isPresent());
        userFetched.get().setId(1L);
        assertEquals(user, userFetched.get());
    }

    @Test
    public void testFindByUsername_DoesNotExist() {
        userRepository.save(getUser());
        Optional<User> userFetched = userRepository.findByUsername("bogus");
        assertFalse(userFetched.isPresent());
    }

    @Test
    public void testExistsByUsername_Exists() {
        userRepository.save(getUser());
        assertTrue(userRepository.existsByUsername("example"));
    }

    @Test
    public void testExistsByUsername_DoesNotExist() {
        userRepository.save(getUser());
        assertFalse(userRepository.existsByUsername("tofu"));
    }

    @Test
    public void testFindByEmail_Exists() {
        User user = getUser();
        userRepository.save(user);
        Optional<User> userFetched = userRepository.findByEmail("example@example.com");
        assertTrue(userFetched.isPresent());
        userFetched.get().setId(1L);
        assertEquals(user, userFetched.get());
    }

    @Test
    public void testFindByEmail_DoesNotExist() {
        userRepository.save(getUser());
        Optional<User> userFetched = userRepository.findByEmail("bogus@bogus.com");
        assertFalse(userFetched.isPresent());
    }

    @Test
    public void testExistsByEmail_Exists() {
        userRepository.save(getUser());
        assertTrue(userRepository.existsByEmail("example@example.com"));
    }

    @Test
    public void testExistsByEmail_DoesNotExist() {
        userRepository.save(getUser());
        assertFalse(userRepository.existsByEmail("bogus@example.com"));
    }

    @Test
    public void testSave_NoCollision() {
        userRepository.save(getUser());
    }

    @Test
    public void testSave_CollisionUsername() {
        userRepository.save(getUser());
        User user2 = new User();
        user2.setUsername("example");
        user2.setEmail("example2@example.com");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }

    @Test
    public void testSave_CollisionEmail() {
        userRepository.save(getUser());
        User user2 = new User();
        user2.setUsername("example2");
        user2.setEmail("example@example.com");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }

    private User getUser() {
        User user = new User();
        user.setUsername("example");
        user.setEmail("example@example.com");
        return user;
    }

}
