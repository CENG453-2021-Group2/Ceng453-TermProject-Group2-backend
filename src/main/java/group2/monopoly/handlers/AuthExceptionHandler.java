package group2.monopoly.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group2.monopoly.auth.exception.BasicAuthException;
import group2.monopoly.mapper.ObjectMapperSingleton;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles {@link BadCredentialsException} and {@link BasicAuthException} exceptions.
 * <br>
 * The response body consists of a JSON object with a "message" attribute and "errors" array.
 */
@ControllerAdvice
public class AuthExceptionHandler {

    public static ObjectMapper mapper = ObjectMapperSingleton.getMapper();

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<JsonNode> handleAccessDeniedException(Exception exception,
                                                                WebRequest webRequest) {
        JsonNode response = mapper.createObjectNode().put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<JsonNode> handleBasicAuthException(BasicAuthException exception,
                                                             WebRequest webRequest) {
        JsonNode response = mapper.createObjectNode().put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

}
