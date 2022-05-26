package group2.monopoly.game.service;

import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameOverException;

public interface IGameEngine {
    void moveStep(Player player) throws GameOverException;

    void purchaseStep(Player player) throws GameOverException,
            GameFaultyMoveException;

}
