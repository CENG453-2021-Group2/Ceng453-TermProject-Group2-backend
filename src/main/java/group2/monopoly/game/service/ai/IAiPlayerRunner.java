package group2.monopoly.game.service.ai;

/**
 * Interface for deciding the AI player's purchases.
 *
 * @see #decideToBuy(Integer, Integer)
 */
public interface IAiPlayerRunner {
    /**
     * Decides the purchase decision of the AI player depending on their current money and the
     * cost of the cell.
     *
     * @param currentMoney the current money of the AI player
     * @param cellCost     the cost of the cell the AI player might buy
     * @return whether the AI player wants to buy the cell
     */
    boolean decideToBuy(Integer currentMoney, Integer cellCost);
}
