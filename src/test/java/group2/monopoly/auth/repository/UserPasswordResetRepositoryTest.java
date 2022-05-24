package group2.monopoly.auth.repository;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.entity.UserPasswordReset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class UserPasswordResetRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserPasswordResetRepository passwordResetRepository;

    private List<User> userList;


    @BeforeEach
    public void setUp() {
        userList = Stream.of(
                User.builder().username("example1").email("example1@example.com").password(
                        "example1").roles(new TreeSet(Set.of("USER"))).build(),
                User.builder().username("example2").email("example2@example.com").password(
                        "example2").roles(new TreeSet(Set.of("USER", "ADMIN"))).build(),
                User.builder().username("example3").email("example3@example.com").password(
                        "example3").roles(new TreeSet(Set.of("USER"))).build()
        ).toList();
        userRepository.saveAll(userList);

        Date date = new Date();
        date.setTime(date.getTime() + 100000);

        UserPasswordReset reset = new UserPasswordReset(userList.get(0),
                UUID.randomUUID(), date);
        passwordResetRepository.save(reset);
    }

    @Test
    void ShouldNot_Throw_When_TokenInserted() {
        Date date = new Date();
        date.setTime(date.getTime() + 120000);

        UserPasswordReset reset = new UserPasswordReset(userList.get(0),
                UUID.randomUUID(), date);
        passwordResetRepository.save(reset);
    }

    @Test
    void Should_ReturnToken_WhenHasToken() {
        assertTrue(passwordResetRepository.findFirstByUserOrderByValidUntilDesc(userList.get(0)).isPresent());
    }

    @Test
    void Should_ReturnMostRecentToken_WhenHasTwoTokens() {
        Date date = new Date();
        date.setTime(date.getTime() + 120000);

        User user = userList.get(0);
        UserPasswordReset reset = new UserPasswordReset(user,
                UUID.randomUUID(), date);
        passwordResetRepository.save(reset);
        Optional<UserPasswordReset> optionalReset =
                passwordResetRepository.findFirstByUserOrderByValidUntilDesc(user);
        assertTrue(optionalReset.isPresent());
        assertEquals(optionalReset.orElseThrow(), reset);
    }

    @Test
    void Should_ReturnNothing_WhenHasNoTokens() {
        Optional<UserPasswordReset> reset =
                passwordResetRepository.findFirstByUserOrderByValidUntilDesc(userList.get(1));
        assertFalse(reset.isPresent());
    }

    @Test
    void Should_ReturnSameToken_WhenHasOneToken() {
        Date date = new Date();
        date.setTime(date.getTime() + 120000);

        User user = userList.get(1);
        UserPasswordReset reset = new UserPasswordReset(user,
                UUID.randomUUID(), date);
        passwordResetRepository.save(reset);
        Optional<UserPasswordReset> token =
                passwordResetRepository.findUserPasswordResetByTokenEquals(reset.getToken());
        assertTrue(token.isPresent());
        assertEquals(token.orElseThrow(), reset);
    }
    @Test
    void Should_ReturnNothingByUuid_WhenHasNoTokens() {
        Optional<UserPasswordReset> reset =
                passwordResetRepository.findUserPasswordResetByTokenEquals(UUID.randomUUID());
        assertFalse(reset.isPresent());
    }
}