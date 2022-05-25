package group2.monopoly.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * It configures the security for the application
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtDecoder jwtDecoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * The password encoder is a function that takes a password and returns a hash of that password
     *
     * @return A new instance of BCryptPasswordEncoder.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(configurer -> configurer
                        .antMatchers("/api/auth/user").authenticated() // user endpoint
                        .antMatchers("/api/auth/**").permitAll() // other auth endpoints
                        .anyRequest().authenticated() // remaining endpoints
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    /**
     * Configure the AuthenticationManagerBuilder with a UserDetailsService and a PasswordEncoder.
     * <p>
     * The AuthenticationManagerBuilder is a helper class that allows easy creation of an
     * AuthenticationManager
     *
     * @param auth AuthenticationManagerBuilder is used to create an AuthenticationManager instance
     *             which is the main Spring Security interface for authenticating a user.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * The `authenticationManagerBean()` function is used to expose the `AuthenticationManager` as a
     * Bean
     *
     * @return AuthenticationManager
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
