package group2.monopoly.auth.service;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.entity.UserPasswordReset;
import group2.monopoly.auth.repository.UserPasswordResetRepository;
import group2.monopoly.auth.repository.UserRepository;
import group2.monopoly.mail.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the correctness of {@link UserPasswordResetService} with injected mock repository,
 * encoder, and email service objects.
 */
@ExtendWith(MockitoExtension.class)
class UserPasswordResetServiceTest {
    private static final List<User> users = List.of(new User("username", "email", "password"));
    private static final Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    private static final Date yesterday = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
    private static final Date now = Date.from(Instant.now());

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordResetRepository resetRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserPasswordResetService resetService;

    @Test
    public void Should_DeletePreviousTokens_When_GeneratingNewToken() {
        resetService.generateNewToken(users.get(0), tomorrow, UUID.randomUUID());
        verify(resetRepository).deleteAllByUser(any());
    }

    @Test
    public void Should_SendEmail_When_GeneratingNewToken() {
        resetService.generateNewToken(users.get(0), tomorrow, UUID.randomUUID());
        verify(emailService).sendSimpleMessage(any(), any(), any());
    }

    @Test
    public void ShouldNot_Throw_When_GeneratingNewTokenWithDefaults() {
        resetService.generateNewToken(users.get(0));
    }

    @Test
    public void Should_CreateNewToken_When_UsernameExists() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(users.get(0)));
        Optional<UserPasswordReset> token = resetService.generateNewToken("user");
        assertTrue(token.isPresent());
    }

    @Test
    public void Should_CreateNewToken_When_EmailExists() {
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(users.get(0)));
        Optional<UserPasswordReset> token = resetService.generateNewToken("email");
        assertTrue(token.isPresent());
    }

    @Test
    public void ShouldNot_CreateNewToken_When_UsernameOrEmailDoesNotExist() {
        Optional<UserPasswordReset> token = resetService.generateNewToken("email");
        assertTrue(token.isEmpty());
    }

    @Test
    public void ShouldNot_ChangePassword_When_NoTokenExists() {
        when(resetRepository.findUserPasswordResetByTokenEquals(any())).thenReturn(Optional.empty());
        assertTrue(resetService.resetPassword(UUID.randomUUID(), "pass").isEmpty());
    }

    @Test
    public void ShouldNot_ChangePassword_When_NoTokenExpired() {
        UserPasswordReset reset = new UserPasswordReset(users.get(0),
                UUID.randomUUID(), yesterday);
//        when(passwordEncoder.encode(any())).thenReturn("pass");
        when(resetRepository.findUserPasswordResetByTokenEquals(any())).thenReturn(Optional.of(reset));
        assertTrue(resetService.resetPassword(UUID.randomUUID(), "pass").isEmpty());
    }

    @Test
    public void Should_ChangePassword_When_TokenValid() {
        UserPasswordReset reset = new UserPasswordReset(users.get(0),
                UUID.randomUUID(), tomorrow);
        when(passwordEncoder.encode(any())).thenReturn("pass");
        when(resetRepository.findUserPasswordResetByTokenEquals(any())).thenReturn(Optional.of(reset));

        assertTrue(resetService.resetPassword(UUID.randomUUID(), "pass").isPresent());
        verify(userRepository).save(any());
        verify(resetRepository).deleteAllByUser(any());
    }
}