package group2.monopoly.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link Entity} class that also implements {@link UserDetails}.
 * <br><br>
 * {@link #isAccountNonLocked()} method checks active {@link #active} field. Other predicates
 * always return true.
 */
@Getter
@Setter
@ToString
@Builder
@Entity(name = "users")
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "user_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name="id")
    private Long id;

    /**
     * Whether the account is active. Deleting an account is done by setting this to false.
     */
    @NonNull
    @Builder.Default
    @Column(name="active", nullable = false)
    private Boolean active = true;

    @NonNull
    @NotBlank
    @Column(name="username", nullable = false, unique = true)
    private String username;

    @NonNull
    @NotBlank
    @Column(name="email", nullable = false, unique = true)
    private String email;

    @NonNull
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 63, nullable = false)
    private String password;

    @ElementCollection
    private Set<String> roles = new HashSet<>();

    /**
     * Constructs a new user.
     *
     * @param username username of the user to be created
     * @param email    email of the user to be created
     * @param password hashed password of the user
     * @param roles    a set of roles the user has such as "ADMIN" or "USER"
     */
    public User(@NonNull String username, @NonNull String email, @NonNull String password,
                @NonNull Set<String> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.active = true;
    }

    /**
     * @see User#User(String, String, String, Set)
     */
    public User(@NonNull String username, @NonNull String email, @NonNull String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = new TreeSet<>(Collections.singleton("USER"));
        this.active = true;
    }


    @Builder
    private User(Long id, @NonNull Boolean active, @NonNull String username, @NonNull String email
            , @NonNull String password, Set<String> roles) {
        this.id = id;
        this.active = active;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Creates a collection of SimpleGrantedAuthority from roles.
     *
     * @return A naturally ordered {@link TreeSet}
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(SimpleGrantedAuthority::new).sorted().collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return getActive();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
