package com.example.demo.player;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PlayerConfig {

    @Bean
    CommandLineRunner commandLineRunner(PlayerRepository playerRepository) {
        return args -> {
            Player kobe = new Player(
                    1L,
                    "Kobe",
                    "Bryant",
                    "Lakers"
            );
            Player lebron = new Player(
                    2L,
                    "Lebron",
                    "James",
                    "Cavaliers"
            );
            Player asdf = new Player(
                    3L,
                    "asdf",
                    "asdf",
                    "asdf"
            );
            playerRepository.saveAll(java.util.Arrays.asList(kobe, lebron, asdf));
        };
    }
}
