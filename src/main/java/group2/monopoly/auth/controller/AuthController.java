package group2.monopoly.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.auth.model.User;
import group2.monopoly.auth.service.UserService;
import group2.monopoly.payload.GenericResponse;
import group2.monopoly.auth.payload.LoginDto;
import group2.monopoly.auth.payload.SignUpDto;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * It contains two endpoints, one for login and one for registration
 */
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json", consumes = "application/json")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.objectMapper = objectMapper;
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
    public ResponseEntity<GenericResponse> authenticatePlayer(@RequestBody LoginDto loginDto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>(GenericResponse.message("logged in"), HttpStatus.OK);
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
    public ResponseEntity<Object> registerPlayer(@Valid @RequestBody SignUpDto signUpDto) {
        ObjectNode responseBody = objectMapper.createObjectNode();

        Either<String, User> result = userService.createUser(signUpDto);

        return result.fold(
                message -> ResponseEntity.badRequest()
                        .body(responseBody.set(
                                "errors",
                                objectMapper.createArrayNode().add(message))
                        ),
                user -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(user)
        );
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
    public ResponseEntity<GenericResponse> forgotPassword(@RequestBody Object identifier) {

        return null;
    }

    /**
     * Resets the password of the account associated with the provided password reset token.
     * <p>
     * For the password change to be successful provided new passwords should match and meet the
     * password requirements.
     *
     * @param obj Contains new password, new password repeated, and the password reset token.
     * @return JSON response notifying that the password reset is successful or describing the
     * errors.
     * @see
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody Object obj) {
        return null;
    }

    /**
     * Returns the account settings of the currently logged-in user.
     *
     * @param obj
     * @return
     */
    @GetMapping("user")
    public ResponseEntity<Object> getUserSettings(@RequestBody Object obj) {
        return null;
    }

    /**
     * Updates the account settings of the currently logged-in user.
     * <p>
     * Password, username, and email can be changed. For any of these (old) password should be
     * supplied as well. Changing username or email invalidates existing password reset tokens.
     *
     * @param obj
     * @return
     */
    @PostMapping("user")
    public ResponseEntity<Object> postUserSettings(@RequestBody Object obj) {
        return null;
    }
}
