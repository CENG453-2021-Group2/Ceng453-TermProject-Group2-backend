package group2.monopoly.game.service;

import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.service.engine.IGameCellPrice;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for computing the score of a player.
 */
@Service
public class GameScoreService {

    private final IGameCellPrice gameCellPriceService;

    public GameScoreService(IGameCellPrice gameCellPriceService) {
        this.gameCellPriceService = gameCellPriceService;
    }

    /**
     * Given a game table, and a player's amount of money and list of indices of their owned
     * properties, computes the player's score.
     *
     * @param table      the game table
     * @param properties the properties belonging to a player
     * @param money      the amount of money a player has
     * @return total score of the player
     */
    public Integer computeScore(GameTableConfiguration table, List<Integer> properties,
                                Integer money) {
        Integer propertyScore = properties
                .stream()
                .filter(table.getPropertyIndices()::contains) // one of the 8 properties
                .map(cellIndex -> gameCellPriceService.getPropertyPrice(table, cellIndex))
                .reduce(0, Integer::sum);
        Integer portScore = properties
                .stream()
                .filter(table.getPortIndices()::contains) // one of the 4 ports
                .map(cellIndex -> gameCellPriceService.getPortPrice(table, cellIndex))
                .reduce(0, Integer::sum);

        return portScore + propertyScore + money;
    }
}
