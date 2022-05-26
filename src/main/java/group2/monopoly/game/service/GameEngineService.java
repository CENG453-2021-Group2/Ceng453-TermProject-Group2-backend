package group2.monopoly.game.service;

import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameOverException;
import group2.monopoly.game.repository.GameRepository;
import group2.monopoly.game.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Integer startingPointCell = 0;
    private final Integer jailCell = 4;
    private final Integer goToJailCell = 12;

    private final Integer jailDuration = 2;

    @Autowired
    public GameEngineService(PlayerRepository playerRepository, GameRepository gameRepository,
                             GameCellPriceService priceService,
                             IDiceGenerator diceService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.priceService = priceService;
        this.diceService = diceService;
    }

    public void moveStep(Player player) throws GameOverException {
        if (player.getRemainingJailTime() > 0) {
            player.setRemainingJailTime(player.getRemainingJailTime() - 1);
            return;
        }

        Game game = player.getGame();
        List<Player> players = game.getPlayers();

        List<Integer> roll = diceService.roll();

        // Handle double dice
        if (roll.get(0).equals(roll.get(1))) {
            player.setSuccessiveDoubles(player.getSuccessiveDoubles() + 1);
            if (player.getSuccessiveDoubles().equals(3)) {
                handleGoToJail(player);
            }
        } else {
            player.setSuccessiveDoubles(0);
        }

        Integer advance = roll.stream().reduce(0, Integer::sum);
        player.setLocation((player.getLocation() + advance) % 16);

        Stream<Integer> ownedPurchasables = getPurchases(players);
        Set<Integer> playerOwnedPurchasables = player.getOwnedPurchasables();

        GameTableConfiguration table = game.getGameTableConfiguration();
        List<Integer> portIndices = table.getPortIndices();
        Integer incomeTaxIndex = table.getIncomeTaxIndex();

        Integer location = player.getLocation();

        if (location.equals(startingPointCell)) {
            payPlayer(player, priceService.getSalary());
        } else if (location.equals(goToJailCell)) {
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

        if (canBuy(player, game, player.getLocation())) {
            player.getOwnedPurchasables().add(player.getLocation());
            chargePlayer(player, game,
                    priceService.getCellPrice(game.getGameTableConfiguration(),
                            player.getLocation()));
        } else {
            throw new GameFaultyMoveException("can not buy cell");
        }
    }

    private void defaultCheck(Player player, Game game) throws GameOverException {
        if (player.getMoney() < 0) {
            game.setCompletionDate(new Date());
            gameRepository.save(game);
            throw new GameOverException(player, game);
        }
    }

    private Stream<Integer> getPurchases(List<Player> players) {
        return players.stream().flatMap(p -> p.getOwnedPurchasables().stream());
    }

    private void payPlayer(Player player, Integer amount) {
        player.setMoney(player.getMoney() + amount);
        playerRepository.save(player);
    }

    private void chargePlayer(Player player, Game game, Integer cost) throws GameOverException {
        player.setMoney(player.getMoney() - cost);
        playerRepository.save(player);
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
                    chargePlayer(player, game, portRent);
                    log.info("charged user " + player.getUser().getUsername() + " " + portRent);
                    payPlayer(p, portRent);
                    log.info("paid user " + p.getUser().getUsername() + " " + portRent);
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
            chargePlayer(player, game, propertyRent);
            log.info("charged player " + player.getId() + " " + propertyRent);
            for (Player p : game.getPlayers()) {
                if (p.getOwnedPurchasables().contains(location)) {
                    payPlayer(p, propertyRent);
                    log.info("paid player " + p.getId() + " " + propertyRent);
                }
            }
        } else {
            log.info("property with index " + player.getLocation() + " is owned by the player");
        }
    }

    private void handleIncomeTaxCell(Player player, Game game) throws GameOverException {
        chargePlayer(player, game, priceService.getIncomeTax());
    }

    private void handleGoToJail(Player player) {
        player.setLocation(jailCell);
        player.setRemainingJailTime(jailDuration);
        player.setSuccessiveDoubles(0);
        playerRepository.save(player);
    }

    public boolean canBuy(Player player, Game game, Integer location) {
        if (Set.of(startingPointCell, goToJailCell, jailCell).contains(location)) {
            return false;
        } else if (getPurchases(game.getPlayers()).toList().contains(location)) {
            return false;
        }
        return player.getMoney() >= priceService.getCellPrice(game.getGameTableConfiguration(),
                location);
    }
}
