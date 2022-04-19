package com.example.demo.leaderboardPackage;

import org.junit.jupiter.api.Assertions;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;


class LeaderboardRecordTest{

}
/*
class PlayerTest {

    @org.junit.jupiter.api.Test
    void getId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Player player = new Player(
                    null,
                    "username",
                    "password",
                    "email"
            );
        });

        AtomicReference<Player> player = new AtomicReference<>(new Player(
                1L,
                "username",
                "password",
                "email"
        ));
        assertEquals(1L, player.get().getId());

        player.set(new Player(
                34567834657843L,
                "username",
                "password",
                "email"
        ));
        assertEquals(34567834657843L, player.get().getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.set(new Player(
                    -12321321L,
                    "username",
                    "password",
                    "email"
            ));
        });

    }

    @org.junit.jupiter.api.Test
    void setId() {
        Player player = new Player(
                1L,
                "username",
                "password",
                "email"
        );

        player.setId(34567834657843L);
        assertEquals(34567834657843L, player.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setId(-12321321L);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player.setId(null);
        });

    }

    @org.junit.jupiter.api.Test
    void getUsername() {
        Player player = new Player(
                1L,
                "username",
                "password",
                "email"
        );
        assertEquals("username", player.getUsername());
    }

    @org.junit.jupiter.api.Test
    void setUsername() {
        Player player = new Player(
                1L,
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
                1L,
                "username",
                "password",
                "email"
        );

        assertEquals("password", player.getPassword());
    }

    @org.junit.jupiter.api.Test
    void setPassword() {
        Player player = new Player(
                1L,
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
                1L,
                "username",
                "password",
                "email"
        );

        assertEquals("email", player.getEmail());
    }

    @org.junit.jupiter.api.Test
    void setEmail() {
        Player player = new Player(
                1L,
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
                1L,
                "username",
                "password",
                "email"
        );
        assertEquals("Player{id=1, username='username', password='password', email='email'}", player.toString());
    }
}

*/