package group2.monopoly.game.service.engine;

import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameOverException;
import group2.monopoly.game.repository.GameRepository;
import group2.monopoly.game.repository.PlayerRepository;
import group2.monopoly.game.service.GameScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Implements {@link IGameCellPrice} interface.
 */
@Service
@Slf4j
public class GameEngineService implements IGameEngine {
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    private final IGameCellPrice priceService;

    private final IDiceGenerator diceService;

    private final GameScoreService scoreService;

    private static final Integer STARTING_POINT_CELL = 0;
    private static final Integer JAIL_CELL = 4;
    private static final Integer GOTO_JAIL_CELL = 12;
    private static final Integer JAIL_DURATION = 2;
    private static final Integer TABLE_SIZE = 16;

    @Autowired
    public GameEngineService(PlayerRepository playerRepository,
                             GameRepository gameRepository,
                             GameCellPriceService priceService,
                             @Qualifier("fileDiceGenerator")
                             IDiceGenerator diceService,
                             GameScoreService scoreService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.priceService = priceService;
        this.diceService = diceService;
        this.scoreService = scoreService;
    }

    /**
     * Executes the movement phase of the player's turn.
     *
     * @param player the player who is in the movement phase of their turn
     * @throws GameOverException if the player goes bankrupt
     */
    public void moveStep(Player player) throws GameOverException {
        if (player.getRemainingJailTime() > 0) {
            log.info("player " + player.getId() + " in jail with remaining time " + player.getRemainingJailTime());
            player.setRemainingJailTime(player.getRemainingJailTime() - 1);
            playerRepository.save(player);
            log.info("after this turn " + player.getRemainingJailTime() + " turns left.");
            return;
        }

        Game game = player.getGame();
        List<Player> players = game.getPlayers();

        List<Integer> roll = diceService.roll();
        player.setLastDice(roll);

        log.info("player " + player.getId() + " rolled " + roll);

        // Handle double dice
        if (roll.get(0).equals(roll.get(1))) {
            player.setSuccessiveDoubles(player.getSuccessiveDoubles() + 1);
            if (player.getSuccessiveDoubles().equals(3)) {
                log.info("player " + player.getId() + " goes to jail for rolling three " +
                         "consecutive doubles.");
                handleGoToJail(player);
                return;
            }
        } else {
            player.setSuccessiveDoubles(0);
        }

        Integer advance = roll.stream().reduce(0, Integer::sum);
        Integer oldLocation = player.getLocation();
        player.setLocation((oldLocation + advance) % TABLE_SIZE);

        log.info("player " + player.getId() + " moved: " + oldLocation + " -->" + player.getLocation());

        Stream<Integer> ownedPurchasables = getPurchases(players);
        Set<Integer> playerOwnedPurchasables = player.getOwnedPurchasables();

        GameTableConfiguration table = game.getGameTableConfiguration();
        List<Integer> portIndices = table.getPortIndices();
        Integer incomeTaxIndex = table.getIncomeTaxIndex();

        Integer location = player.getLocation();

        if (oldLocation > player.getLocation()) {
            // only happens if the player went past the starting point
            log.info("player " + player.getId() + " went past the starting point");
            payPlayer(player, priceService.getSalary());
        } else if (location.equals(GOTO_JAIL_CELL)) {
            log.info("player " + player.getId() + " goes to jail for landing on 'Go to Jail' cell" +
                     ".");
            handleGoToJail(player);
        }
        if (location.equals(incomeTaxIndex)) {
            handleIncomeTaxCell(player, game);
        } else if (ownedPurchasables.anyMatch(p -> p.equals(location))) {
            // Purchased cell
            if (portIndices.contains(location)) {
                handlePortCell(player, game, portIndices, playerOwnedPurchasables);
            } else {
                handlePropertyCell(player, game, playerOwnedPurchasables);
            }
        }
        playerRepository.save(player);
    }

    /**
     * Executes the purchase phase of the player's turn.
     *
     * @param player the player who is in the purchase step of their turn
     * @throws GameFaultyMoveException if the cell can't be purchased by the player
     */
    public void purchaseStep(Player player) throws
            GameFaultyMoveException {
        Game game = player.getGame();

        Integer location = player.getLocation();
        if (canBuy(player, game, location)) {
            player.getOwnedPurchasables().add(location);
            log.info("player " + player.getId() + " buys " + location);
            try {
                chargePlayer(player, game,
                        priceService.getCellPrice(game.getGameTableConfiguration(),
                                location));
            } catch (GameOverException e) {
                log.error("The player should not go bankrupt by trying to purchase a cell!");
                e.printStackTrace();
            }
        } else {
            throw new GameFaultyMoveException("can not buy cell");
        }
    }

    /**
     * Checks the amount of money the player has.
     * <br><br>
     * If the player is in negative balance, the game ends. This method should be called by
     * methods that decrease the amount of money a player has.
     *
     * @param player the player to be checked
     * @param game   the game
     * @throws GameOverException if the player is in negative balance.
     */
    private void defaultCheck(Player player, Game game) throws GameOverException {
        if (player.getMoney() < 0) {
            game.setCompletionDate(new Date());
            gameRepository.save(game);
            game.getPlayers().forEach(p -> {
                p.setScore(
                        scoreService.computeScore(
                                game.getGameTableConfiguration(),
                                p.getOwnedPurchasables().stream().toList(),
                                player.getMoney()));
                playerRepository.save(p);
            });
            throw new GameOverException(player, game);
        }
    }

    /**
     * Returns the union of purchases of the given list of players.
     *
     * @param players the list of players
     * @return the flattened list of indices of the purchases of the players
     */
    private Stream<Integer> getPurchases(List<Player> players) {
        return players.stream().flatMap(p -> p.getOwnedPurchasables().stream());
    }

    /**
     * Utility method that does the exact opposite of {@link #chargePlayer(Player, Game, Integer)}.
     *
     * @param player the player
     * @param amount the amount to pay to the player
     */
    private void payPlayer(Player player, Integer amount) {
        Integer oldMoney = player.getMoney();
        player.setMoney(oldMoney + amount);
        log.info("paid player " + player.getId() + " " + amount + " " + oldMoney + " -> " + player.getMoney());
        playerRepository.save(player);
    }

    /**
     * Decreases the player's money amount.
     *
     * @param player the player to be charged
     * @param game   the game
     * @param cost   the amount of money to be charged
     * @throws GameOverException if the player goes bankrupt
     */
    @Override
    public void chargePlayer(Player player, Game game, Integer cost) throws GameOverException {
        Integer oldMoney = player.getMoney();
        player.setMoney(oldMoney - cost);
        playerRepository.save(player);
        log.info("charged player " + player.getId() + " " + cost + " " + oldMoney + " -> " + player.getMoney());
        defaultCheck(player, game);
    }

    /**
     * Charges the player with the port rent if it does not belong to the player.
     *
     * @param player                  the player that landed on a purchased port
     * @param game                    the game
     * @param playerOwnedPurchasables the player's owned purchasables
     * @throws GameOverException if the player goes bankrupt
     */
    @Override
    public void handlePortCell(Player player,
                               Game game,
                               List<Integer> portIndices,
                               Set<Integer> playerOwnedPurchasables) throws GameOverException {
        Integer location = player.getLocation();
        List<Player> players = game.getPlayers();
        if (!playerOwnedPurchasables.contains(location)) {
            // Not owner
            for (Player p : players) {
                if (p.getOwnedPurchasables().contains(location)) {
                    Integer count = 0;
                    // owner
                    for (Integer i : p.getOwnedPurchasables()) {
                        if (portIndices.contains(i)) {
                            count++;
                        }
                    }
                    Integer portRent = priceService.getPortRent(game.getGameTableConfiguration(),
                            player.getLocation()
                            , count);
                    log.info("player " + player.getId() + " landed on player " + p.getId() + "'s " +
                             "port");
                    chargePlayer(player, game, portRent);
                    payPlayer(p, portRent);
                    playerRepository.save(p);
                    break;
                }
            }
        }
        log.info("port with index " + player.getLocation() + " is owned by the player themself");
    }

    /**
     * Charges the player with the property rent if it does not belong to the player.
     *
     * @param player                  the player that landed on a purchased property
     * @param game                    the game
     * @param playerOwnedPurchasables the player's owned purchasables
     * @throws GameOverException if the player goes bankrupt
     */
    @Override
    public void handlePropertyCell(Player player,
                                   Game game,
                                   Set<Integer> playerOwnedPurchasables) throws GameOverException {
        Integer location = player.getLocation();
        if (!playerOwnedPurchasables.contains(location)) {
            // Property owned by someone else
            Integer propertyRent = priceService.getPropertyRent(game.getGameTableConfiguration(),
                    location);
            for (Player p : game.getPlayers()) {
                if (p.getOwnedPurchasables().contains(location)) {
                    log.info("player " + player.getId() + " landed on player " + p.getId() + "'s " +
                             "property");
                    chargePlayer(player, game, propertyRent);
                    payPlayer(p, propertyRent);
                    break;
                }
            }
        } else {
            log.info("property with index " + player.getLocation() + " is owned by the player " +
                     "themself");
        }
    }

    /**
     * Charges user for landing on the income tax cell
     *
     * @param player the player landed on income tax cell
     * @param game   the game
     * @throws GameOverException if the player goes bankrupt
     */
    @Override
    public void handleIncomeTaxCell(Player player, Game game) throws GameOverException {
        log.info("player " + player.getId() + " landed on property tax cell");
        chargePlayer(player, game, priceService.getIncomeTax());
    }

    /**
     * Sends to player to jail.
     *
     * @param player the player
     */
    @Override
    public void handleGoToJail(Player player) {
        player.setLocation(JAIL_CELL);
        player.setRemainingJailTime(JAIL_DURATION);
        player.setSuccessiveDoubles(0);
        playerRepository.save(player);
    }

    /**
     * Checks whether the given player of the given game can purchase the given location.
     *
     * @param player   the player
     * @param game     the ongoing game
     * @param location the index of the cell the player wants to buy
     * @return true if the player can buy the current cell
     */
    public boolean canBuy(Player player, Game game, Integer location) {
        Integer incomeTaxIndex = game.getGameTableConfiguration().getIncomeTaxIndex();
        if (Set.of(STARTING_POINT_CELL, GOTO_JAIL_CELL, JAIL_CELL, incomeTaxIndex).contains(location)) {
            return false;
        } else if (getPurchases(game.getPlayers()).toList().contains(location)) {
            return false;
        }
        return player.getMoney() >= priceService.getCellPrice(game.getGameTableConfiguration(),
                location);
    }

    /**
     * Afflicts the player with crippling debt ($10000).
     *
     * @param player the victim of the crippling debt.
     * @param game   the game
     * @throws GameOverException if the player goes bankrupt (they certainly will)
     */
    @Override
    public void nukeGame(Player player, Game game) throws GameOverException {
        log.info("quite something happening to player " + player.getId());
        chargePlayer(player, game, 10000);
    }
}
