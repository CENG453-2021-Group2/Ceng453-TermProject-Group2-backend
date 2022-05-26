package group2.monopoly.auth.service;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.payload.SignUpDto;
import group2.monopoly.auth.payload.UserSettingsChangeDTO;
import group2.monopoly.auth.repository.UserRepository;
import group2.monopoly.security.CustomUserDetailsService;
import io.vavr.control.Either;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.control.Either.left;

/**
 * Service class for user authenticated related tasks.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Checks supplied raw password.
     *
     * @param userDetails User the password is being checked against.
     * @param password Raw password
     * @return true if raw password matches the user's encoded password after encoding.
     */
    public boolean passwordMatches(UserDetails userDetails, String password) {
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    /**
     * Checks supplied raw password.
     *
     * @param user user object
     * @param password raw password
     * @return true if the user's password field is the encoded form of the raw password.
     * @see #passwordMatches(UserDetails, String)
     */
    public boolean passwordMatches(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Promotes {@link UserDetails} object to a {@link User}.
     * @param userDetails user details object
     * @return user object with matching username
     */
    public User promoteToUser(UserDetails userDetails) {
        return userRepository.findUserByUsername(userDetails.getUsername());
    }

    /**
     * Promotes {@link JwtAuthenticationToken} object to a {@link User}.
     * @param authentication jwt authentication object
     * @return currently authenticated user
     * @see #promoteToUser(UserDetails)
     */
    public User promoteToUser(JwtAuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getTokenAttributes();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(attributes.get("username").toString());
        return promoteToUser(userDetails);
    }

    /**
     * Creates a new user from the DTO object. Role of the User is hardcoded to "USER".
     *
     * @param signUpDto DTO object used for creating the user
     * @return Either the newly created user or the error message
     * @see #createUser(String, String, String)
     */
    public Either<String, User> createUser(@NotNull SignUpDto signUpDto) {
        return createUser(signUpDto.getUsername(), signUpDto.getEmail(), signUpDto.getPassword());
    }

    /**
     * Creates a user from given arguments. The User role is hardcoded to "USER"
     *
     * @param username username
     * @param email    email
     * @param password <b>unhashed</b> password
     * @return Either the newly created user or the error message
     * @see #createUser(SignUpDto)
     */
    public Either<String, User> createUser(String username, String email, String password) {
        return createUser(username, email, password, Collections.singleton("USER"));
    }


    /**
     * Creates a user from given arguments. The User roles can be specified
     *
     * @param username username
     * @param email    email
     * @param password <b>unhashed</b> password
     * @param roles    A collection of roles for the user such as "USER" or "ADMIN"
     * @return Either the newly created user or the error message
     */
    private Either<String, User> createUser(String username, String email, String password,
                                            Collection<String> roles) {
        if (userRepository.existsByEmail(email)) {
            return left("email exists");
        }

        if (userRepository.existsByUsername(username)) {
            return left("username exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .roles(new TreeSet<>(roles))
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);

        return Either.right(user);
    }

    /**
     * Creates a superuser from given arguments. The User role is hardcoded to "ADMIN" and "USER".
     * <br>
     * This method is not meant to be used by an endpoint.
     *
     * @param username username
     * @param email    email
     * @param password <b>unhashed</b> password
     * @return Either the newly created user or the error message
     * @see #createUser(String, String, String)
     */
    public Either<String, User> createSuperUser(String username, String email, String password) {
        return createUser(username, email, password,
                Stream.of("USER", "ADMIN").collect(Collectors.toSet()));
    }

//    public Either<String, User> update

    public Either<String, User> updateUser(@NonNull User user, UserSettingsChangeDTO dto) {
        Either<String, User> eitherUser = Either.right(user);
        eitherUser = eitherUser
                .flatMap(u ->
                        Optional.ofNullable(dto.getUsername())
                                .map(username -> updateUsername(u, username))
                                .orElse(Either.right(u))
                )
                .flatMap(u ->
                        Optional.ofNullable(dto.getEmail())
                                .map(email -> updateEmail(u, email))
                                .orElse(Either.right(u))
                )
                .flatMap(u ->
                        Optional.ofNullable(dto.getNewPassword())
                                .map(password -> {
                                    if (dto.getPassword().equals(dto.getNewPassword())) {
                                        return Either.<String, User>left("new password is the same as the previous one");
                                    }
                                    u.setPassword(password);
                                    return Either.<String, User>right(u);
                                })
                                .orElse(Either.right(u))
                )
                .peek(userRepository::save);
        return eitherUser;
    }

    private Either<String, User> updateEmail(User user, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.filter(u -> !u.equals(user))
                .map(u -> Either.<String, User>left("email exists"))
                .orElse(Either.right(user))
                .flatMap(u -> {
                    if (user.getEmail().equals(email)) {
                        return Either.left("new email is the same as the previous one");
                    }
                    user.setEmail(email);
                    return Either.right(user);
                });
    }


    private Either<String, User> updateUsername(User user, String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        return existingUser.filter(u -> !u.equals(user))
                .map(u -> Either.<String, User>left("user exists"))
                .orElse(Either.right(user))
                .flatMap(u -> {
                    if (user.getUsername().equals(username)) {
                        return Either.left("new username is the same as the previous one");
                    }
                    user.setUsername(username);
                    return Either.right(u);
                });
    }
}
