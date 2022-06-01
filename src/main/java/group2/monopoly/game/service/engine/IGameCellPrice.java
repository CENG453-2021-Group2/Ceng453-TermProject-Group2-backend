package group2.monopoly.game.service.engine;

import group2.monopoly.game.entity.GameTableConfiguration;

/**
 * Interface providing methods for calculation of prices, rents and other costs in the game.
 */
public interface IGameCellPrice {
    /**
     * Returns the price of the property in the specified index.
     * <br><br>
     * The cell is assumed to be a property cell and not another type of cell.
     *
     * @param table the game table
     * @param tableIndex the index of the property cell on the table
     * @return the price of the property
     */
    Integer getPropertyPrice(GameTableConfiguration table, Integer tableIndex);

    /**
     * Returns the price of the port in the specified index.
     * <br><br>
     * The cell is assumed to be a port cell and not another type of cell.
     *
     * @param table the game table
     * @param tableIndex the index of the port cell on the table
     * @return the price of the port
     */
    Integer getPortPrice(GameTableConfiguration table, Integer tableIndex);

    /**
     * Returns the income tax cost.
     *
     * @return income tax
     */
    Integer getIncomeTax();

    /**
     * Returns the salary a player gets after completing a full tour of the table.
     *
     * @return the salary
     */
    Integer getSalary();

    /**
     * Returns the price of a purchasable cell in the specified index.
     * <br><br>
     * The cell can be either a port or a property.
     *
     * @param table the game table
     * @param tableIndex the index of the purchasable cell on the table
     * @return the price of the cell
     */
    Integer getCellPrice(GameTableConfiguration table, Integer tableIndex);

    /**
     * Returns the rent of a property.
     *
     * @param table the game table
     * @param tableIndex the index of the property
     * @return the rent a player should pay to the owner after landing on this cell
     */
    Integer getPropertyRent(GameTableConfiguration table, Integer tableIndex);

    /**
     * Returns the rent of a port.
     *
     * @param table the game table
     * @param tableIndex the index of the port
     * @param portCount the number of ports owned by the owner of this port
     * @return the rent a player should pay to the owner after landing on this cell
     */
    Integer getPortRent(GameTableConfiguration table, Integer tableIndex, Integer portCount);
}
