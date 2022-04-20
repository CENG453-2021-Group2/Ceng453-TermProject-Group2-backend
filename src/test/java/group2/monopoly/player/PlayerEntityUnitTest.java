package group2.monopoly.player;

import org.junit.jupiter.api.Assertions;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PlayerEntityUnitTest {


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
    }
}