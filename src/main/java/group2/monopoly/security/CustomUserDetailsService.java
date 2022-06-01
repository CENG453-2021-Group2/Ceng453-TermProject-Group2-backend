package group2.monopoly.security;

import group2.monopoly.auth.repository.UserRepository;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Implements the {@link UserDetailsService} interface.
 * <br><br>
 * {@link #loadUserByUsername(String)} method returns a {@link group2.monopoly.auth.entity.User}
 * object, as the class implements {@link UserDetails} interface.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the {@link group2.monopoly.auth.entity.User} if such user exists with the given
     * username.
     *
     * @param username The username of the user to load.
     * @return Found user.
     * @throws UsernameNotFoundException if no matching user exists.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with username: " + username)
                );
    }
}
