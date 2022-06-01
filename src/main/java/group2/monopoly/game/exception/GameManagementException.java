package group2.monopoly.game.exception;

/**
 * Exception that is thrown when a user is not authorized to interact with the requested game, or
 * the interaction can't be conducted for another reason such as a game with the same name
 * existing while creating a new game.
 */
public class GameManagementException extends Exception {
    public GameManagementException(String message) {
        super(message);
    }
}
