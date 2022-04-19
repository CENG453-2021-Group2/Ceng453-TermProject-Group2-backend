package group2.monopoly.controller;

import group2.monopoly.entity.Role;
import group2.monopoly.entity.User;
import group2.monopoly.payload.GenericResponse;
import group2.monopoly.payload.LoginDto;
import group2.monopoly.payload.SignUpDto;
import group2.monopoly.repository.RoleRepository;
import group2.monopoly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json", consumes = "application/json")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>(GenericResponse.message("success"), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        System.out.println("hello");
        System.out.println(signUpDto);
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>(GenericResponse.error("username exists"), HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>(GenericResponse.error("email exists"), HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>(GenericResponse.message("success"), HttpStatus.OK);
    }

}
