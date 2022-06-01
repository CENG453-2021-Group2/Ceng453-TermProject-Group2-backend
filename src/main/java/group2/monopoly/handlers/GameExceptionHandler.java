package group2.monopoly.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.exception.GameOverException;
import group2.monopoly.mapper.ObjectMapperSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles in-game exceptions {@link GameOverException}, {@link GameFaultyMoveException}, to
 * produce appropriate response messages.
 */
@ControllerAdvice
@Slf4j
public class GameExceptionHandler {
    public static ObjectMapper mapper = ObjectMapperSingleton.getMapper();

    /**
     * Handles {@link GameOverException} exception.
     * <br><br>
     * The response body contains message field, loser user, and the final state of the game.
     *
     * @param exception  exception to handle
     * @param webRequest the associated web request
     * @return generated response object describing the game state at the end of the game.
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({GameOverException.class})
    public ResponseEntity<JsonNode> handleGameOverException(GameOverException exception,
                                                            WebRequest webRequest) {
        log.info("Game over.");
        ObjectNode response = mapper.createObjectNode()
                .put("message", "game over")
                .<ObjectNode>set("game", mapper.valueToTree(exception.getGame()))
                .set("loser", mapper.valueToTree(exception.getPlayer()));
        return ResponseEntity.ok().body(response);
    }

    /**
     * Handles {@link GameFaultyMoveException} exception.
     *
     * @param exception  exception to handle
     * @param webRequest the associated web request
     * @return generated response object with the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({GameFaultyMoveException.class})
    public ResponseEntity<JsonNode> handleGameFaultyMoveException(GameFaultyMoveException exception,
                                                                  WebRequest webRequest) {
        log.info("Invalid move");
        ObjectNode response = mapper.createObjectNode()
                .put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
