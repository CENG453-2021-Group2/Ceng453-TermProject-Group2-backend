package group2.monopoly.game.service;

import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.service.engine.IGameCellPrice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameScoreService {

    private final IGameCellPrice gameCellPriceService;

    public GameScoreService(IGameCellPrice gameCellPriceService) {
        this.gameCellPriceService = gameCellPriceService;
    }

    public Integer computeScore(GameTableConfiguration table, List<Integer> properties, Integer money) {
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
