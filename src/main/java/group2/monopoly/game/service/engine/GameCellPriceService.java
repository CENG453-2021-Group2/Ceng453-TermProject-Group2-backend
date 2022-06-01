package group2.monopoly.game.service.engine;

import group2.monopoly.game.entity.GameTableConfiguration;
import org.springframework.stereotype.Service;

/**
 * Implements {@link IGameCellPrice} interface with sensible defaults. See the implemented
 * methods for more details.
 */
@Service
public class GameCellPriceService implements IGameCellPrice {
    /**
     * Returns the price of the property in the specified index.
     * <br><br>
     * The cell is assumed to be a property cell and not another type of cell. The formula used
     * for calculating the price is (PROPERTY INDEX) * 50 + 100, where PROPERTY INDEX is the
     * index of this cell in the game table's property cells list.
     *
     * @param table      the game table
     * @param tableIndex the index of the property cell on the table
     * @return the price of the property
     */
    @Override
    public Integer getPropertyPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPropertyIndices().contains(tableIndex)) {
            return 100 + (table.getPropertyIndices().indexOf(tableIndex)) * 50;
        }
        throw new IllegalArgumentException("given cell is not a property");
    }

    /**
     * Returns the price of the port in the specified index, which is 250 in this implementation.
     * <br><br>
     * The cell is assumed to be a port cell and not another type of cell.
     *
     * @param table      the game table
     * @param tableIndex the index of the port cell on the table
     * @return the price of the port
     */
    @Override
    public Integer getPortPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPortIndices().contains(tableIndex)) {
            return 250;
        }
        throw new IllegalArgumentException("given cell is not a port");
    }

    /**
     * Returns the income tax cost, which is 50 in this implementation.
     *
     * @return income tax
     */
    @Override
    public Integer getIncomeTax() {
        return 50;
    }

    /**
     * Returns the salary a player gets after completing a full tour of the table, which is 100
     * in this implementation.
     *
     * @return the salary
     */
    @Override
    public Integer getSalary() {
        return 100;
    }

    /**
     * Returns the price of a purchasable cell in the specified index.
     * <br><br>
     * The cell can be either a port or a property.
     *
     * @param table      the game table
     * @param tableIndex the index of the purchasable cell on the table
     * @return the price of the cell
     */
    @Override
    public Integer getCellPrice(GameTableConfiguration table, Integer tableIndex) {
        if (table.getPortIndices().contains(tableIndex)) {
            return getPortPrice(table, tableIndex);
        } else if (table.getPropertyIndices().contains(tableIndex)) {
            return getPropertyPrice(table, tableIndex);
        }
        throw new IllegalArgumentException("cell is neither a port nor a property");
    }

    /**
     * Returns the rent of a property.
     * <br><br>
     * The exact formula used for the calculation is (PROPERTY PRICE) / 10
     *
     * @param table      the game table
     * @param tableIndex the index of the property
     * @return the rent a player should pay to the owner after landing on this cell
     */
    @Override
    public Integer getPropertyRent(GameTableConfiguration table, Integer tableIndex) {
        return getPropertyPrice(table, tableIndex) / 10;
    }

    /**
     * Returns the rent of a port.
     * <br><br>
     * The exact formula used for the calculation is: (PORT PRICE) * (PORT COUNT) / 10
     *
     * @param table      the game table
     * @param tableIndex the index of the port
     * @param portCount  the number of ports owned by the owner of this port
     * @return the rent a player should pay to the owner after landing on this cell
     */
    @Override
    public Integer getPortRent(GameTableConfiguration table, Integer tableIndex,
                               Integer portCount) {
        return getPortPrice(table, tableIndex) / 10 * portCount;
    }
}
