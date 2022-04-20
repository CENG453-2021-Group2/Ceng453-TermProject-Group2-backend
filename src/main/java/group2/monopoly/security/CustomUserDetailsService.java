package group2.monopoly.security;
import group2.monopoly.player.PlayerRepository;
import group2.monopoly.player.Player;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * It implements the UserDetailsService interface and overrides the loadUserByUsername method
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    // A constructor.
    public CustomUserDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
        Player player =
                playerRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Player not found with username: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(player.getUsername())
                .roles("USER") // TODO: Should not be hardcoded.
                .password(player.getPassword())
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        /* TODO(DK): /api/auth/register should grant user role. We should implement a CLI option to
         * create super users like is the case in Django (python manage.py createsuperuser).
         */
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return null;
    }
}
