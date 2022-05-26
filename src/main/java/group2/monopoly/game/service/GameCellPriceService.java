package group2.monopoly.game.service;

import group2.monopoly.game.entity.GameTableConfiguration;
import org.springframework.stereotype.Service;

@Service
public class GameCellPriceService implements IGameCellPrice {
    @Override
    public Integer getPropertyPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPropertyIndices().contains(tableIndex)) {
            return 100 + (table.getPropertyIndices().indexOf(tableIndex)) * 50;
        }
        throw new IllegalArgumentException("given cell is not a property");
    }

    @Override
    public Integer getPortPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPortIndices().contains(tableIndex)) {
            return 250;
        }
        throw new IllegalArgumentException("given cell is not a port");
    }

    @Override
    public Integer getIncomeTax() {
        return 50;
    }

    @Override
    public Integer getSalary() {
        return 100;
    }

    @Override
    public Integer getCellPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPortIndices().contains(tableIndex)) {
            return getPortPrice(table, tableIndex);
        } else if (table.getPropertyIndices().contains(tableIndex)) {
            return getPropertyPrice(table, tableIndex);
        }
        throw new IllegalArgumentException("cell is neither a port nor a property");
    }

    @Override
    public Integer getPropertyRent(GameTableConfiguration table, Integer tableIndex) {
        return getPropertyPrice(table, tableIndex) / 10;
    }

    @Override
    public Integer getPortRent(GameTableConfiguration table, Integer tableIndex, Integer portCount) {
        return getPortPrice(table, tableIndex) / 10 * portCount;
    }
}
