package group2.monopoly.game.exception;

import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.Player;
import lombok.Getter;

/**
 * Exception that is thrown when one of the players of a game goes bankrupt.
 */
@Getter
public class GameOverException extends Exception {
    private final Player player;
    private final Game game;
    public GameOverException(Player player, Game game) {
        this.player = player;
        this.game = game;
    }
}
