package group2.monopoly.game.service.ai;

public interface IAiPlayerRunner {
    boolean decideToBuy(Integer currentMoney, Integer cellCost);
}
