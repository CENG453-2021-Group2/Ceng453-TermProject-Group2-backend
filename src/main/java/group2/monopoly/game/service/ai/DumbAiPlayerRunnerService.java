package group2.monopoly.game.service.ai;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 *
 */
@Service
public class DumbAiPlayerRunnerService implements IAiPlayerRunner {

    /**
     * Randomly deliberates the purchase decision depending on the given parameters.
     * <br><br>
     * The AI calculates the "willingness-to-buy" depending on the given parameters. Then the AI
     * rolls a pseudo-random number between 0 and 1. If the rolled number is greater than the
     * willingness, they decide buy the cell.
     *
     * @param currentMoney the current money of the AI player
     * @param cellCost     the cost of the cell the AI player might buy
     * @return whether the AI player wants to buy the cell
     */
    @Override
    public boolean decideToBuy(Integer currentMoney, Integer cellCost) {
        if (currentMoney < cellCost) {
            return false;
        }

        double current = currentMoney.doubleValue();
        double cost = cellCost.doubleValue();
        double remainingVsCurrentRatio = (current - cost) / current;
        double currentVsCostRatio = current / cost;
        double probabilityToBuy = Math.min(0.9, Math.max(0.1, Math.pow(remainingVsCurrentRatio,
                currentVsCostRatio)));

        Random rand = new Random();
        double v = rand.nextDouble();
        return v < probabilityToBuy;
    }
}
