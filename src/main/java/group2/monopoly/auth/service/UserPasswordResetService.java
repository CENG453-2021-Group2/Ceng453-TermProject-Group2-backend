package group2.monopoly.auth.service;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.entity.UserPasswordReset;
import group2.monopoly.auth.repository.UserPasswordResetRepository;
import group2.monopoly.auth.repository.UserRepository;
import group2.monopoly.mail.EmailService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Service providing password reset features including token generation.
 */
@Service
@Slf4j
public class UserPasswordResetService {
    private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;

    final private UserRepository userRepository;
    final private UserPasswordResetRepository userPasswordResetRepository;
    final private EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserPasswordResetService(UserRepository userRepository,
                                    UserPasswordResetRepository userPasswordResetRepository,
                                    EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPasswordResetRepository = userPasswordResetRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new password reset token and invalidates old one.
     * <br>
     * The newly generated token is sent to the user's registered email address.
     *
     * @param user       The account object
     * @param validUntil Time until when the token is valid
     * @param uuid       Password reset token value
     * @return Password reset token object
     */
    public UserPasswordReset generateNewToken(@NonNull User user, Date validUntil, UUID uuid) {
        userPasswordResetRepository.deleteAllByUser(user);
        UserPasswordReset newToken = new UserPasswordReset();
        newToken.setUser(user);
        newToken.setToken(uuid);
        newToken.setValidUntil(validUntil);
        userPasswordResetRepository.save(newToken);

        final String mailSubject = "Monopoly Password Reset Token";

        log.info(user.getEmail() + "\n" + mailSubject + "\n" + newToken.getToken());

        emailService.sendSimpleMessage(user.getEmail(), mailSubject,
                newToken.getToken().toString());
        log.info("password reset email sent");
        return newToken;
    }

    /**
     * Creates a new password reset token and invalidates old one given a User.
     * <br>
     * A random UUID is while generating the token, and the token is valid for two days.
     *
     * @param user The account object
     * @return Generated password reset token if such account exists
     * @see #generateNewToken(User, Date, UUID)
     */
    public UserPasswordReset generateNewToken(@NonNull User user) {
        Date twoDaysLater = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));
        return generateNewToken(user, twoDaysLater, UUID.randomUUID());
    }

    /**
     * Creates a new password reset token and invalidates old one given an account identifier.
     * <br>
     * A random UUID is while generating the token, and the token is valid for two days.
     *
     * @param identifier Email or username of an account
     * @return Generated password reset token if such account exists
     * @see #generateNewToken(User, Date, UUID)
     */
    public Optional<UserPasswordReset> generateNewToken(@NonNull @NotBlank String identifier) {
        log.info("trying to generate new token for user with identifier " + identifier);
        Optional<User> userByUsername = userRepository.findByUsername(identifier);
        if (userByUsername.isPresent()) {
            log.info("found user by username " + userByUsername.orElseThrow());
            return userByUsername.map(this::generateNewToken);
        }
        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        userByEmail.ifPresent(u -> log.info("found user by username " + u));
        return userByEmail.map(this::generateNewToken);
    }

    /**
     * Resets the password of the account corresponding to the given token.
     * <br>
     * Password reset commences only if the supplied token exists, corresponds to a user, and it
     * is not past its expiration date. Reset token is invalidated during the process.
     *
     * @param token       UUID token
     * @param newPassword new password
     * @return User object if the supplied token was valid.
     */
    public Optional<User> resetPassword(@NonNull UUID token, String newPassword) {
        Optional<UserPasswordReset> passwordReset =
                userPasswordResetRepository.findUserPasswordResetByTokenEquals(token)
                        .filter(t -> t.getValidUntil().after(new Date()));
        if (passwordReset.isPresent()) {
            Optional<User> optionalUser = passwordReset.map(UserPasswordReset::getUser);
            optionalUser.ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                userPasswordResetRepository.deleteAllByUser(user);
            });
            return optionalUser;
        }
        return Optional.empty();
    }
}
