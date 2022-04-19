package group2.monopoly.security;
import group2.monopoly.player.PlayerRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomPlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public CustomPlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadPlayerByPlayername(String username) throws PlayernameNotFoundException {
        Player player =
                playerRepository.findByPlayername(username).orElseThrow(() -> new PlayernameNotFoundException("Player not found with username: " + username));
        return new org.springframework.security.core.playerdetails.Player(player.getEmail(),
                player.getPassword(), mapRolesToAuthorities(player.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
