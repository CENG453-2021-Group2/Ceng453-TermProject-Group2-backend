package group2.monopoly.auth.controller;


import group2.monopoly.payload.GenericResponse;
import group2.monopoly.payload.LoginDto;
import group2.monopoly.payload.SignUpDto;
import group2.monopoly.player.Player;
import group2.monopoly.player.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

/**
 * It contains two endpoints, one for login and one for registration
 */
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json", consumes = "application/json")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final PlayerRepository playerRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          PlayerRepository playerRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * It takes a username and password, checks if they are valid, and if they are, it sets the current
     * user to the user with that username
     * 
     * @param loginDto This is the object that will be sent to the server. It contains the username and
     * password.
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
     * If the username or email already exists, return an error message, otherwise create a new player
     * and save it to the database
     * 
     * @param signUpDto This is the object that will be sent to the server.
     * @return A response entity with a generic response object.
     */
    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerPlayer(@Valid @RequestBody SignUpDto signUpDto) {
        if (playerRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>(GenericResponse.error("username exists"), HttpStatus.BAD_REQUEST);
        }
        if (playerRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>(GenericResponse.error("email exists"), HttpStatus.BAD_REQUEST);
        }

        Player player = new Player();
        player.setUsername(signUpDto.getUsername());
        player.setEmail(signUpDto.getEmail());
        player.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        playerRepository.save(player);

        return new ResponseEntity<>(GenericResponse.message("signed up"), HttpStatus.OK);
    }

}
