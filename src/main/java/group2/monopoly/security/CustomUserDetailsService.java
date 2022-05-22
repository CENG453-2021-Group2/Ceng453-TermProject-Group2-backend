package group2.monopoly.security;

import group2.monopoly.auth.repository.UserRepository;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * It implements the UserDetailsService interface and overrides the loadUserByUsername method
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // A constructor.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * If the user exists, return a UserDetails object with the username, password, and role of the
     * user. Otherwise, throw an exception.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with " + "username: " + username)
                );
    }
}
