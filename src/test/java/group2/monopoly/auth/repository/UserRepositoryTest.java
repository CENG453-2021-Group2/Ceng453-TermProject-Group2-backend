package group2.monopoly.auth.repository;

import group2.monopoly.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        Stream.of(
                User.builder().username("example1").email("example1@example.com").password(
                        "example1").roles(new TreeSet(Set.of("USER"))).build(),
                User.builder().username("example2").email("example2@example.com").password(
                        "example2").roles(new TreeSet(Set.of("USER", "ADMIN"))).build()
        ).forEach(userRepository::save);
    }

    @Test
    void ShouldNot_Throw_When_UserInserted() {
        userRepository.save(new User("username", "email", "password"));
    }

    @Test
    void Should_FindUserByEmail_WhenUserExists() {
        assertTrue(userRepository.findByEmail("example1@example.com").isPresent());
    }

    @Test
    void ShouldNot_FindUserByEmail_WhenUserDoesNotExist() {
        assertFalse(userRepository.findByEmail("example3@example.com").isPresent());
    }

    @Test
    void Should_FindUserByUsername_WhenUserExists() {
        assertTrue(userRepository.findByUsername("example1").isPresent());
    }

    @Test
    void ShouldNot_FindUserByUsername_WhenUserDoesNotExist() {
        assertFalse(userRepository.findByUsername("example3").isPresent());
    }

    @Test
    void Should_ExistsByEmailBeTrue_WhenUserExists() {
        assertTrue(userRepository.existsByEmail("example1@example.com"));
    }

    @Test
    void Should_ExistsByEmailBeFalse_WhenUserDoesNotExist() {
        assertFalse(userRepository.existsByEmail("example3@example.com"));
    }
}