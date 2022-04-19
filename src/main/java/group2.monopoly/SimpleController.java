package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.player.Player;

import java.util.List;

//@RestController
public class SimpleController {
    @GetMapping("/")
    public String homePage() {
        return "Hello World!";
    }

    @GetMapping("/can")
    public String canPage() {
        return "Can I have a cookie?";
    }

    @GetMapping("/players")
    public Player getPlayers() {
        return new Player(1L, "alpylmz", "jkjk", "ylmz.alp.e@gmail.com");
    }


    @GetMapping("/playerslist")
    public List<Player> getPlayersList() {
        return List.of(new Player(1L, "alpylmz", "jkjk", "ylmz.alp.e@gmail.com"),
                new Player(2L, "alpylmz", "jkjk", "xxx"));
    }

}
