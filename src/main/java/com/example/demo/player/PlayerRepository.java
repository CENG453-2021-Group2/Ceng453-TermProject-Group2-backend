package com.example.demo.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //@Query(value = "SELECT * FROM player WHERE email = ?1", nativeQuery = true)
    Optional<Player> findPlayerByEmail(String name);
}
