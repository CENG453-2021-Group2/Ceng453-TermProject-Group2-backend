package group2.monopoly.game.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import group2.monopoly.game.service.manager.GameManagerService;
import group2.monopoly.game.service.engine.IGameCellPrice;
import group2.monopoly.game.service.engine.IGameEngine;
import group2.monopoly.mapper.ObjectMapperSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for mapping in-game requests.
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


    /**
     * Lists games available to the user
     *
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @return List of games
     */
    @GetMapping("")
    public List<Game> getAvailableGames(Authentication authentication) {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        return gameManager.getAvailableGames(user);
    }

    /**
     * Creates a new game with the supplied game name.
     *
     * @param dto            {@link GameCreateDTO} object
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @return created {@link Game}
     * @throws GameManagementException if a game with such name already exists
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Game createGame(@RequestBody GameCreateDTO dto,
                           Authentication authentication) throws GameManagementException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        return gameManager.createGame(user, dto.getName());
    }

    /**
     * Gets the specified {@link Game}.
     *
     * @param id             Id of the {@link Game} object
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @return {@link Game} object with the given id
     * @throws GameManagementException if the user has no access to such game
     */
    @GetMapping("/{id}")
    public Game getGame(@PathVariable("id") Long id, Authentication authentication) throws GameManagementException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        return gameManager.getGame(user, id);
    }

    /**
     * Deletes a game.
     *
     * @param id             Id of the {@link Game} object
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @throws GameManagementException if the game is already completed or the user is not
     *                                 authorized to delete the game
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable("id") Long id, Authentication authentication) throws GameManagementException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        gameManager.deleteGame(user, gameManager.getGame(user, id));
    }

    /**
     * Simulates a whole turn in game
     * <br><br>
     * The human player is in the purchase phase of their turn. Depending on the user's decision
     * in the request, the purchase is made. Following this, AI player plays a whole turn
     * (consisting of movement phase and purchase phase). Finally, the movement phase of the
     * human player is simulated and the resulting game state is sent as response to the user,
     * waiting for a new request to decide the action in the next purchase phase.
     *
     * @param id             Id of the {@link Game} object
     * @param dto            {@link GameInteractionDTO} object for specifying the action in
     *                                                 purchase phase
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @return The resulting game state
     * @throws GameManagementException if the user has no access to such game
     * @throws GameFaultyMoveException if the user wants to buy a cell that can't be purchased
     * (already purchased, not a property etc.)
     * @throws GameOverException       if a player goes bankrupt within simulated the game turn
     */
    @PostMapping("/{id}")
    public Game interactWithGame(@PathVariable("id") Long id, @RequestBody GameInteractionDTO dto
            , Authentication authentication) throws GameManagementException,
            GameFaultyMoveException, GameOverException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
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
        }

        gameEngine.moveStep(robot);

        if (gameEngine.canBuy(robot, game, robot.getLocation())) {
            Integer cellPrice = cellPriceService.getCellPrice(game.getGameTableConfiguration(),
                    robot.getLocation());
            if (aiPlayerRunner.decideToBuy(robot.getMoney(), cellPrice)) {
                gameEngine.purchaseStep(robot);
            }
        }

        gameEngine.moveStep(player);

        return game;
    }

    /**
     * Nukes the game by afflicting the specified player with crippling debt.
     * <br><br>
     * The player surely will never financially recover from this.
     *
     * @param id             Id of the {@link Game} object
     * @param authentication {@link Authentication} object supplied by Spring Security
     * @param params         A {@link Map} with key "id" and value of a player id
     * @return The resulting game state if the player survives
     * @throws GameManagementException if the user has no access to such game
     * @throws GameOverException       if the victim goes bankrupt
     */
    @PostMapping("/{id}/nuke")
    public Game nukeGame(@PathVariable("id") Long id, Authentication authentication,
                         @RequestBody Map<String, Integer> params) throws GameManagementException
            , GameOverException {
        User user = userService.promoteToUser((JwtAuthenticationToken) authentication);
        Game game = gameManager.getGame(user, id);
        gameEngine.nukeGame(game.getPlayers().get(params.get("id") - 1), game);
        return game;
    }

}

