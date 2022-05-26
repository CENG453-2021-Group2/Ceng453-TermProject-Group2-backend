package group2.monopoly.game.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.auth.entity.User;
import group2.monopoly.auth.service.UserService;
import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameManagementException;
import group2.monopoly.game.exception.GameOverException;
import group2.monopoly.game.payload.GameCreateDTO;
import group2.monopoly.game.payload.GameInteractionDTO;
import group2.monopoly.game.service.AiPlayerRunner.IAiPlayerRunner;
import group2.monopoly.game.service.GameManagerService;
import group2.monopoly.game.service.IGameCellPrice;
import group2.monopoly.game.service.IGameEngine;
import group2.monopoly.mapper.ObjectMapperSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * It contains two endpoints, one for login and one for registration
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/game", produces = "application/json", consumes = "application/json")
public class GameController {

    private final ObjectMapper objectMapper = ObjectMapperSingleton.getMapper();
    private final UserService userService;

    private final GameManagerService gameManager;

    private final IGameEngine gameEngine;

    private final IAiPlayerRunner aiPlayerRunner;

    private final IGameCellPrice cellPriceService;

    @Autowired
    public GameController(UserService userService, GameManagerService gameManager,
                          IGameEngine gameEngine,
                          IAiPlayerRunner aiPlayerRunner, IGameCellPrice cellPriceService) {
        this.userService = userService;
        this.gameManager = gameManager;
        this.gameEngine = gameEngine;
        this.aiPlayerRunner = aiPlayerRunner;
        this.cellPriceService = cellPriceService;
    }


    @GetMapping("/")
    public List<Game> getAvailableGames(Authentication authentication) {
        User user = userService.promoteToUser((UserDetails) authentication);
        return gameManager.getAvailableGames(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public Game createGame(@RequestBody GameCreateDTO dto,
                                                 Authentication authentication) {
        User user = userService.promoteToUser((UserDetails) authentication);
        return gameManager.createGame(user, dto.getName());
    }

    @GetMapping("/{id}")
    public Game getGame(@PathVariable("id") Long id, Authentication authentication) throws GameManagementException {
        User user = userService.promoteToUser((UserDetails) authentication);
        return gameManager.getGame(user, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable("id") Long id, Authentication authentication) throws GameManagementException {
        User user = userService.promoteToUser((UserDetails) authentication);
        gameManager.deleteGame(user, gameManager.getGame(user, id));
    }

    @PostMapping("/{id}")
    public Game interactWithGame(@PathVariable("id") Long id, @RequestBody GameInteractionDTO dto
            , Authentication authentication) throws GameManagementException,
            GameFaultyMoveException, GameOverException {
        User user = userService.promoteToUser((UserDetails) authentication);
        Game game = gameManager.getGame(user, id);
        List<Player> players = game.getPlayers();

        Player player;
        Player robot;

        if (players.get(0).getUser() == null) {
            player = players.get(1);
            robot = players.get(0);
        } else {
            player = players.get(0);
            robot = players.get(1);
        }

        if (dto.getBuy()) {
            gameEngine.purchaseStep(player);

            gameEngine.moveStep(robot);
        }
        Integer cellPrice = cellPriceService.getCellPrice(game.getGameTableConfiguration(),
                robot.getLocation());
        if (aiPlayerRunner.decideToBuy(robot.getMoney(), cellPrice)) {
            gameEngine.purchaseStep(robot);
        }

        gameEngine.moveStep(player);

        return game;
    }
}

