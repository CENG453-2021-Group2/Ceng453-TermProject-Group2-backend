package group2.monopoly.game.service.ai;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DumbAiPlayerRunnerService implements IAiPlayerRunner {
    @Override
    public boolean decideToBuy(Integer currentMoney, Integer cellCost) {
        if (currentMoney < cellCost) {
            return false;
        }

        double current = currentMoney.doubleValue();
        double cost = cellCost.doubleValue();
        double remainingVsCurrentRatio = (current - cost) / current;
        double currentVsCostRatio = current / cost;
        Double probabilityToBuy = Math.min(0.9, Math.max(0.1, Math.pow(remainingVsCurrentRatio,
                currentVsCostRatio)));

        Random rand = new Random();
        double v = rand.nextDouble();
        return v < probabilityToBuy;
    }
}
