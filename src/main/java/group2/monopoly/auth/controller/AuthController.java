package group2.monopoly.auth.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.auth.exception.BasicAuthException;
import group2.monopoly.auth.model.User;
import group2.monopoly.auth.payload.LoginDto;
import group2.monopoly.auth.payload.PasswordResetRequestDTO;
import group2.monopoly.auth.payload.SignUpDto;
import group2.monopoly.auth.payload.UserSettingsChangeDTO;
import group2.monopoly.auth.service.UserPasswordResetService;
import group2.monopoly.auth.service.UserService;
import group2.monopoly.mapper.ObjectMapperSingleton;
import io.vavr.control.Either;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * It contains two endpoints, one for login and one for registration
 */
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json", consumes = "application/json")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final ObjectMapper objectMapper = ObjectMapperSingleton.getMapper();

    private final UserPasswordResetService userPasswordResetService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          UserPasswordResetService userPasswordResetService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userPasswordResetService = userPasswordResetService;
    }

    /**
     * It takes a username and password, checks if they are valid, and if they are, it sets the
     * current
     * user to the user with that username
     *
     * @param loginDto This is the object that will be sent to the server. It contains the
     *                 username and
     *                 password.
     * @return A generic response with a message of "success"
     */
    @PostMapping("/login")
    public ResponseEntity<ObjectNode> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(objectMapper.createObjectNode().put("message", "logged in"));
    }

    /**
     * If the username or email already exists, return an error message, otherwise create a new
     * player
     * and save it to the database
     *
     * @param signUpDto DTO object with sign up info.
     * @return A response entity with either the created user or error description.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpDto signUpDto) throws BasicAuthException {
        if (signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new BasicAuthException("passwords should match");
        }
        Either<String, User> result = userService.createUser(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getOrElseThrow(BasicAuthException::new));
    }

    /**
     * Given a username or email, sends a password reset token to the account owner's email.
     * <p>
     * This endpoint responds the same regardless of whether the requested account exists. This
     * is done to prevent account enumeration by abusing this endpoint.
     *
     * @param identifier Username or email of the user.
     * @return JSON response notifying a password reset token is sent if such user exists.
     */
    @PostMapping("/forgotPassword")
    public ResponseEntity<ObjectNode> forgotPassword(@RequestBody @NonNull String identifier) {
        userPasswordResetService.generateNewToken(identifier);
        return ResponseEntity.ok().body(objectMapper.createObjectNode().put("message", "password " +
                "reset token will be sent to your email if such account exists"));
    }

    /**
     * Resets the password of the account associated with the provided password reset token.
     * <p>
     * For the password change to be successful provided new passwords should match and meet the
     * password requirements.
     *
     * @param passwordResetRequestDTO Contains new password, new password repeated, and the
     *                                password reset token.
     * @return JSON response notifying that the password reset is successful or describing the
     * errors.
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<JsonNode> resetPassword(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) throws BasicAuthException {
        Optional<User> optionalUser =
                userPasswordResetService.resetPassword(passwordResetRequestDTO.getToken(),
                        passwordResetRequestDTO.getPassword());
        User user = optionalUser.orElseThrow(() -> new BasicAuthException("token either does not " +
                "exist " +
                "or is expired"));
        return ResponseEntity.ok().body(objectMapper.createObjectNode().put("message",
                "password successfully changed for user " + user.getUsername()));
    }

    /**
     * Returns the account settings of the currently logged-in user.
     *
     * @return JSON representation of the user
     */
    @GetMapping("user")
    public ResponseEntity<User> getUserSettings() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the account settings of the currently logged-in user.
     * <p>
     * Password, username, and email can be changed. For any of these (old) password should be
     * supplied as well. Changing username or email invalidates existing password reset tokens.
     *
     * @param dto user account settings to be updated
     * @return JSON representation of the user
     */
    @PostMapping("user")
    public ResponseEntity<Object> postUserSettings(@Valid @RequestBody UserSettingsChangeDTO dto) throws BasicAuthException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Authentication
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        // Password validation
        String newPassword = dto.getNewPassword();
        String confirmNewPassword = dto.getConfirmNewPassword();

        if (newPassword == null && confirmNewPassword == null || newPassword != null && newPassword.equals(confirmNewPassword)) {
            if (dto.getEmail() == null && dto.getNewPassword() == null && dto.getUsername() == null) {
                throw new BasicAuthException("At least one of 'username', 'email', " +
                        "'newPassword' fields should be provided.");
            }
            Either<String, User> eitherUser = userService.updateUser(user, dto);
            return ResponseEntity.ok(eitherUser.getOrElseThrow(BasicAuthException::new));
        }
        throw new BasicAuthException("new passwords should be matching");
    }
}
