package group2.monopoly.game.exception;

/**
 * Exception that is thrown when a player tries to purchase a cell that can't be purchased.
 */
public class GameFaultyMoveException extends Exception {
    public GameFaultyMoveException(String message) {
        super(message);
    }

}
