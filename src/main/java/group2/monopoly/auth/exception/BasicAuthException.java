package group2.monopoly.auth.exception;

/**
 * Simple exceptions with error messages.
 * <br>
 * {@link group2.monopoly.handlers.AuthExceptionHandler} handles these exceptions and creates appropriate JSON responses.
 *
 * @see group2.monopoly.handlers.AuthExceptionHandler
 */
public class BasicAuthException extends Exception {
    public BasicAuthException(String message) {
        super(message);
    }
}
