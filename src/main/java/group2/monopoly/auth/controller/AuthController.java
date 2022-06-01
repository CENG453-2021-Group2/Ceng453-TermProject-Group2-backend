package group2.monopoly.auth.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.exception.BasicAuthException;
import group2.monopoly.auth.payload.LoginDto;
import group2.monopoly.auth.payload.PasswordResetRequestDTO;
import group2.monopoly.auth.payload.SignUpDto;
import group2.monopoly.auth.payload.UserSettingsChangeDTO;
import group2.monopoly.auth.service.UserPasswordResetService;
import group2.monopoly.auth.service.UserService;
import group2.monopoly.mapper.ObjectMapperSingleton;
import group2.monopoly.security.jwt.JwtHelper;
import group2.monopoly.validation.CustomGroupSequence;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for mapping authentication related endpoints.
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/auth", produces = "application/json", consumes = "application/json")
public class AuthController {

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    private final UserService userService;

    private final ObjectMapper objectMapper = ObjectMapperSingleton.getMapper();

    private final UserPasswordResetService userPasswordResetService;

    @Autowired
    public AuthController(UserDetailsService userDetailsService, JwtHelper jwtHelper,
                          AuthenticationManager authenticationManager, UserService userService,
                          UserPasswordResetService userPasswordResetService) {
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.userService = userService;
        this.userPasswordResetService = userPasswordResetService;
    }

    /**
     * Tries to authenticate the supplied credentials.
     *
     * @param loginDto Validated {@link LoginDto} object
     * @return Response with JSON body containing the JWT token
     * @see LoginDto
     */
    @PostMapping("/login")
    public ResponseEntity<JsonNode> authenticateUser(@Validated(CustomGroupSequence.class) @RequestBody LoginDto loginDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
        if (!userService.passwordMatches(userDetails, loginDto.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }
        User user = userService.promoteToUser(userDetails);


        Map<String, String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId().toString());
        String jwt = jwtHelper.createJwtForClaims(user.getUsername(), claims);
        return ResponseEntity.ok().body(objectMapper.createObjectNode().put("token", jwt));
    }

    /**
     * Registers a new user.
     * <br><br>
     * The DTO fields are validated with the validators specified in {@link SignUpDto}.
     *
     * @param signUpDto validated {@link SignUpDto} object
     * @return A response entity with either the created user or error description
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Validated(CustomGroupSequence.class) @RequestBody SignUpDto signUpDto) throws BasicAuthException {
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(objectMapper.createObjectNode().put("message"
                    , "One or more fields could not be validated.").set("errors",
                    objectMapper.createObjectNode().set("password",
                            objectMapper.createArrayNode().add("passwords should be matching"))));
        }
        Either<String, User> result = userService.createUser(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getOrElseThrow(BasicAuthException::new));
    }

    /**
     * Given a username or email, sends a password reset token to the account owner's email.
     * <br><br>
     * This endpoint responds the same regardless of whether the requested account exists. This
     * is done to prevent account enumeration by abusing this endpoint.
     *
     * @param identifier Username or email of the user
     * @return JSON response notifying a password reset token is sent if such user exists
     */
    @PostMapping("/forgotPassword")
    public ResponseEntity<ObjectNode> forgotPassword(@RequestBody @NonNull JsonNode identifier) throws BasicAuthException {
        JsonNode node = identifier.get("identifier");
        if (node == null) {
            throw new BasicAuthException("identifier can't be null");
        }

        String s = node.textValue();
        if (s == null) {
            throw new BasicAuthException("identifier should be a string");
        }

        if (userPasswordResetService.generateNewToken(s).isEmpty()) {
            log.info("identifier:" + identifier + "not found");
        }
        return ResponseEntity
                .ok()
                .body(objectMapper.createObjectNode()
                        .put("message",
                                "password reset token will be sent to your email if such account " +
                                "exists"));
    }

    /**
     * Resets the password of the account associated with the provided password reset token.
     * <br><br>
     * For the password change to be successful provided new passwords should match and meet the
     * password requirements.
     *
     * @param passwordResetRequestDTO a {@link PasswordResetRequestDTO} object
     * @return JSON response notifying that the password reset is successful or describing the
     * errors
     * @see PasswordResetRequestDTO
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<JsonNode> resetPassword(@Validated(CustomGroupSequence.class) @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) throws BasicAuthException {
        Optional<User> optionalUser =
                userPasswordResetService.resetPassword(UUID.fromString(passwordResetRequestDTO.getToken()),
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
    public ResponseEntity<Object> getUserSettings(Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        Map<String, Object> attributes = token.getTokenAttributes();
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(attributes.get("username").toString());
        return ResponseEntity.ok(userService.promoteToUser(userDetails));
    }

    /**
     * Updates the account settings of the currently logged-in user.
     * <br><br>
     * Password, username, and email can be changed. For any of these (old) password should be
     * supplied as well. Changing username or email invalidates existing password reset tokens.
     *
     * @param dto user account settings to be updated
     * @return JSON representation of the user
     * @throws BasicAuthException if the update fails
     * @see UserSettingsChangeDTO
     */
    @PostMapping("user")
    public ResponseEntity<Object> postUserSettings(@Validated(CustomGroupSequence.class) @RequestBody UserSettingsChangeDTO dto, Authentication authentication) throws BasicAuthException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        if (!userService.passwordMatches(user, dto.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }

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
