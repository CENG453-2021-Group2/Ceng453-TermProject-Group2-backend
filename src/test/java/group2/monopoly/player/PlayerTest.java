package group2.monopoly.player;

import group2.monopoly.player.Player;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PlayerTest {

    @org.junit.jupiter.api.Test
    void getId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Player player = new Player(
                    "username",
                    "password",
                    "email"
            );
        });

        AtomicReference<Player> player = new AtomicReference<>(new Player(
                "username",
                "password",
                "email"
        ));
        assertEquals(1L, player.get().getId());


    }


    @org.junit.jupiter.api.Test
    void getUsername() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );
        assertEquals("username", player.getUsername());
    }

    @org.junit.jupiter.api.Test
    void setUsername() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );

        player.setUsername("newUsername");
        assertEquals("newUsername", player.getUsername());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setUsername(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setUsername("");
        });
    }

    @org.junit.jupiter.api.Test
    void getPassword() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );

        assertEquals("password", player.getPassword());
    }

    @org.junit.jupiter.api.Test
    void setPassword() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );

        player.setPassword("newPassword");
        assertEquals("newPassword", player.getPassword());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setPassword(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setPassword("");
        });
    }

    @org.junit.jupiter.api.Test
    void getEmail() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );

        assertEquals("email", player.getEmail());
    }

    @org.junit.jupiter.api.Test
    void setEmail() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );

        player.setEmail("newEmail");
        assertEquals("newEmail", player.getEmail());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setEmail(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setEmail("");
        });
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        Player player = new Player(
                "username",
                "password",
                "email"
        );
        assertEquals("Player{id=1, username='username', password='password', email='email'}", player.toString());
    }
}