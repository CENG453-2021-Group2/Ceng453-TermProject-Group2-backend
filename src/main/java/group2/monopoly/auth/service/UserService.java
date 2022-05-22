package group2.monopoly.auth.service;

import group2.monopoly.auth.model.User;
import group2.monopoly.auth.payload.SignUpDto;
import group2.monopoly.auth.repository.UserRepository;
import io.vavr.collection.Tree;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class for user authenticated related tasks.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user from the DTO object. Role of the User is hardcoded to "USER".
     * @param signUpDto DTO object used for creating the user
     * @return Either the newly created user or the error message
     * @see #createUser(String, String, String)
     */
    public Either<String, User> createUser(SignUpDto signUpDto) {
        return createUser(signUpDto.getUsername(), signUpDto.getEmail(), signUpDto.getPassword());
    }

    /**
     * Creates a user from given arguments. The User role is hardcoded to "USER"
     * @param username username
     * @param email email
     * @param password <b>unhashed</b> password
     * @return Either the newly created user or the error message
     * @see #createUser(SignUpDto)
     */
    public Either<String, User> createUser(String username, String email, String password) {
        return createUser(username, email, password, Collections.singleton("USER"));
    }


    /**
     * Creates a user from given arguments. The User roles can be specified
     * @param username username
     * @param email email
     * @param password <b>unhashed</b> password
     * @param roles A collection of roles for the user such as "USER" or "ADMIN"
     * @return Either the newly created user or the error message
     */
    private Either<String, User> createUser(String username, String email, String password,
                                            Collection<String> roles) {
        if (userRepository.existsByEmail(email)) {
            return Either.left("email exists");
        }

        if (userRepository.existsByEmail(username)) {
            return Either.left("username exists");
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
     * @param username username
     * @param email email
     * @param password <b>unhashed</b> password
     * @return Either the newly created user or the error message
     * @see #createUser(String, String, String)
     */
    public Either<String, User> createSuperUser(String username, String email, String password) {
        return createUser(username, email, password,
                Stream.of("USER", "ADMIN").collect(Collectors.toSet()));
    }
}
