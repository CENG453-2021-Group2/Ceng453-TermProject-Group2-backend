package group2.monopoly.auth.service;

import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.payload.UserSettingsChangeDTO;
import group2.monopoly.auth.repository.UserRepository;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    private List<User> userList;


    @BeforeEach()
    public void setUp() {
        userList = Stream.of(
                User.builder().username("example1").email("example1@example.com").password(
                        "example1").roles(new TreeSet(Set.of("USER"))).build(),
                User.builder().username("example2").email("example2@example.com").password(
                        "example2").roles(new TreeSet(Set.of("USER", "ADMIN"))).build(),
                User.builder().username("example3").email("example3@example.com").password(
                        "example3").roles(new TreeSet(Set.of("USER"))).build()
        ).toList();
    }

    @Test
    public void ShouldNot_CreateUser_When_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        Either<String, User> user = userService.createUser("username", "email", "password");
        assertEquals(user, Either.left("email exists"));
    }

    @Test
    public void ShouldNot_CreateUser_When_UsernameExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        Either<String, User> user = userService.createUser("username", "email", "password");
        assertEquals(user, Either.left("email exists"));
    }

    @Test
    public void ShouldNot_CreateUser_When_PasswordNull() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        assertThrows(NullPointerException.class, () -> userService.createUser("username", "email", null));
    }

    @Test
    public void Should_CreateUser_When_ArgumentsCorrect() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("pass");
        assertTrue(userService.createUser("username", "email", "password").isRight());
    }

    @Test
    public void Should_HaveAdminRole_When_CreatedSuperuser() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("pass");
        Either<String, User> superUser = userService.createSuperUser("username", "email",
                "password");
        assertTrue(superUser.isRight());
        assertTrue(superUser.getOrElseThrow(() -> new Exception()).getRoles().contains("ADMIN"));
    }

    @Test
    public void ShouldNot_HaveAdminRole_When_CreatedNormalUser() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("pass");
        Either<String, User> user = userService.createUser("username", "email",
                "password");
        assertTrue(user.isRight());
        assertFalse(user.getOrElseThrow(() -> new Exception()).getRoles().contains("ADMIN"));
    }

    @Test
    public void ShouldNot_UpdateAnything_When_NoFieldsGiven() {
        UserSettingsChangeDTO dto = new UserSettingsChangeDTO();
        assertTrue(userService.updateUser(userList.get(0), dto).isRight()); // No error message
    }

    @Test
    public void ShouldNot_UpdateAnything_When_UsernameSame() {
        UserSettingsChangeDTO dto = new UserSettingsChangeDTO();
        User user = userList.get(0);
        dto.setUsername(user.getUsername());
        Either<String, User> result = userService.updateUser(user, dto);
        assertTrue(result.isLeft());
        assertEquals(result, Either.left("new username is the same as the previous one"));
    }

    @Test
    public void ShouldNot_UpdateAnything_When_EmailSame() {
        UserSettingsChangeDTO dto = new UserSettingsChangeDTO();
        User user = userList.get(0);
        dto.setEmail(user.getEmail());
        Either<String, User> result = userService.updateUser(user, dto);
        assertTrue(result.isLeft());
        assertEquals(result, Either.left("new email is the same as the previous one"));
    }

    @Test
    public void ShouldNot_UpdateAnything_When_PasswordSame() {
        UserSettingsChangeDTO dto = new UserSettingsChangeDTO();
        User user = userList.get(0);
        dto.setPassword(user.getPassword());
        dto.setNewPassword(user.getPassword());
        Either<String, User> result = userService.updateUser(user, dto);
        assertTrue(result.isLeft());
        assertEquals(result, Either.left("new password is the same as the previous one"));
    }

    @Test
    public void Should_ChangeAll_When_AllDifferent() {
        UserSettingsChangeDTO dto = new UserSettingsChangeDTO();
        User user = userList.get(0);
        dto.setUsername("foo");
        dto.setEmail("bar");
        dto.setPassword("baz");
        dto.setNewPassword("bazz");
        dto.setConfirmNewPassword("bazz");
        Either<String, User> result = userService.updateUser(user, dto);
        assertTrue(result.isRight());
        result.map(
                u -> {
                    assertEquals(u.getUsername(), "foo");
                    assertEquals(u.getEmail(), "bar");
                    assertEquals(u.getPassword(), "bazz");
                    return true;
                }
        );

        verify(userRepository).save(any()); // Actually saved?
    }

}