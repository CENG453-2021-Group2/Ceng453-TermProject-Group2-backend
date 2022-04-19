package group2.monopoly.exceptions;

public class UserExistsException extends RuntimeException{
    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
