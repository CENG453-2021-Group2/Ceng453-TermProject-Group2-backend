package group2.monopoly.exceptions;

public class DatabaseInitializeException extends RuntimeException{
    public DatabaseInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
