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

    public void purchaseStep(Player player) throws GameOverException,
            GameFaultyMoveException {
        Game game = player.getGame();

        Integer location = player.getLocation();
        if (canBuy(player, game, location)) {
            player.getOwnedPurchasables().add(location);
            log.info("player " + player.getId() + " buys " + location);
            chargePlayer(player, game,
                    priceService.getCellPrice(game.getGameTableConfiguration(),
                            location));
        } else {
            throw new GameFaultyMoveException("can not buy cell");
        }
    }

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

    private Stream<Integer> getPurchases(List<Player> players) {
        return players.stream().flatMap(p -> p.getOwnedPurchasables().stream());
    }

    private void payPlayer(Player player, Integer amount) {
        Integer oldMoney = player.getMoney();
        player.setMoney(oldMoney + amount);
        log.info("paid player " + player.getId() + " " + amount + " " + oldMoney + " -> " + player.getMoney());
        playerRepository.save(player);
    }

    private void chargePlayer(Player player, Game game, Integer cost) throws GameOverException {
        Integer oldMoney = player.getMoney();
        player.setMoney(oldMoney - cost);
        playerRepository.save(player);
        log.info("charged player " + player.getId() + " " + cost + " " + oldMoney + " -> " + player.getMoney());
        defaultCheck(player, game);
    }

    private void handlePortCell(Player player,
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
        log.info("port with index " + player.getLocation() + " is owned by the player");
    }

    private void handlePropertyCell(Player player,
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
            log.info("property with index " + player.getLocation() + " is owned by the player");
        }
    }

    private void handleIncomeTaxCell(Player player, Game game) throws GameOverException {
        log.info("player " + player.getId() + " landed on property tax cell");
        chargePlayer(player, game, priceService.getIncomeTax());
    }

    private void handleGoToJail(Player player) {
        player.setLocation(JAIL_CELL);
        player.setRemainingJailTime(JAIL_DURATION);
        player.setSuccessiveDoubles(0);
        playerRepository.save(player);
    }

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

    @Override
    public void nukeGame(Player player, Game game) throws GameOverException {
        log.info("quite something happening to player " + player.getId());
        chargePlayer(player, game, 10000);
    }
}
